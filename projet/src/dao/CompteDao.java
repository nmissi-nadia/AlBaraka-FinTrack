package dao;

import entities.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import utilitaire.exceptions.*;

public class CompteDao implements GenInDao<Compte> {
    private final Connection connection;

    public CompteDao(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void create(Compte compte) {
        String sql = "INSERT INTO compte (numero, solde, id_client, type_compte, decouvert_autorise, taux_interet) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getNumero());
            stmt.setDouble(2, compte.getSolde());
            stmt.setObject(3, compte.getIdClient());

            if (compte instanceof CompteCourant cc) {
                stmt.setString(4, "COURANT");
                stmt.setDouble(5, cc.getDecouvert());
                stmt.setNull(6, java.sql.Types.NUMERIC);
            } else if (compte instanceof CompteEpargne ce) {
                stmt.setString(4, "EPARGNE");
                stmt.setNull(5, java.sql.Types.NUMERIC);
                stmt.setDouble(6, ce.getTauxInteret());
            } else {
                throw new IllegalArgumentException("Type de compte inconnu : " + compte.getClass().getSimpleName());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Compte findById(UUID id) {
        String sql = "SELECT * FROM compte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type_compte");
                if ("COURANT".equalsIgnoreCase(type)) {
                    return new CompteCourant(
                            rs.getObject("id", java.util.UUID.class),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getObject("id_client",java.util.UUID.class),
                            rs.getDouble("decouvert_autorise")
                    );
                } else if ("EPARGNE".equalsIgnoreCase(type)) {
                    return new CompteEpargne(
                            rs.getObject("id", java.util.UUID.class),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getObject("id_client",java.util.UUID.class),
                            rs.getDouble("taux_interet")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Compte> findAll() {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte"; // pas 'comptes', sauf si ta table s'appelle vraiment ainsi
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String type = rs.getString("type_compte");

                if ("COURANT".equalsIgnoreCase(type)) {
                    comptes.add(new CompteCourant(
                            rs.getObject("id", java.util.UUID.class),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getObject("id_client",java.util.UUID.class),
                            rs.getDouble("decouvert_autorise")
                    ));
                } else if ("EPARGNE".equalsIgnoreCase(type)) {
                    comptes.add(new CompteEpargne(
                            rs.getObject("id", java.util.UUID.class),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getObject("id_client",java.util.UUID.class),
                            rs.getDouble("taux_interet")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comptes;
    }
    public List<Compte> findByClientId(int clientId) {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM compte WHERE client_id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comptes.add(mapResultSetToCompte(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comptes;
    }



    @Override
    public void update(Compte compte) {
        String sql = "UPDATE compte SET numero=?, solde=?, id_client=?, type_compte=?, decouvert_autorise=?, taux_interet=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getNumero());
            stmt.setDouble(2, compte.getSolde());
            stmt.setObject(3, compte.getIdClient());

            if (compte instanceof CompteCourant courant) {
                stmt.setString(4, "COURANT");
                stmt.setDouble(5, courant.getDecouvert());
                stmt.setNull(6, Types.NUMERIC);
            } else if (compte instanceof CompteEpargne epargne) {
                stmt.setString(4, "EPARGNE");
                stmt.setNull(5, Types.NUMERIC);
                stmt.setDouble(6, epargne.getTauxInteret());
            } else {
                throw new IllegalArgumentException("Type de compte inconnu.");
            }

            stmt.setObject(7, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(Compte compte) {
        String sql = "DELETE FROM compte WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Compte mapResultSetToCompte(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        String numero = rs.getString("numero_compte");
        double solde = rs.getDouble("solde");
        UUID clientId = rs.getObject("client_id",java.util.UUID.class);
        String typeCompte = rs.getString("type_compte");

        if ("COURANT".equalsIgnoreCase(typeCompte)) {
            double decouvert = rs.getDouble("decouvert_autorise");
            return new CompteCourant(id, numero, solde, clientId, decouvert);
        }

        else if ("EPARGNE".equalsIgnoreCase(typeCompte)) {
            double tauxInteret = rs.getDouble("taux_interet");
            return new CompteEpargne(id, numero, solde, clientId, tauxInteret);
        }

        throw new SQLException("Type de compte inconnu : " + typeCompte);
    }
    public List<String> comptesInactifs() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT cp.numero, c.nom, MAX(t.date) as derniere_tx
        FROM compte cp
        JOIN client c ON cp.id_client = c.id
        LEFT JOIN transactions t\s
          ON cp.id = t.compte_source OR cp.id = t.compte_destination
        GROUP BY cp.numero, c.nom
        HAVING MAX(t.date) IS NULL OR MAX(t.date) < now() - interval '6 months'
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add("Compte: " + rs.getString("numero") +
                        " | Client: " + rs.getString("nom") +
                        " | Dernière Tx: " + rs.getTimestamp("derniere_tx"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public List<String> getAlertesComptes() throws SQLException {
        List<String> alertes = new ArrayList<>();
        double seuilSolde = 1000.0;
        int joursInactivite = 90;
        String sql = "SELECT c.id_compte, c.solde, " +
                "(SELECT MAX(t.date_transaction) FROM transactions t WHERE t.id_compte = c.id_compte) as derniere_transaction " +
                "FROM comptes c";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String idCompte = rs.getString("id_compte");
                double solde = rs.getDouble("solde");
                Date derniereTransaction = rs.getDate("derniere_transaction");

                if (solde < seuilSolde) {
                    alertes.add("Compte " + idCompte + " : solde bas (" + solde + " MAD)");
                }
                if (derniereTransaction != null) {
                    long joursEcoules =
                            java.time.temporal.ChronoUnit.DAYS.between(
                                    derniereTransaction.toLocalDate(),
                                    java.time.LocalDate.now()
                            );
                    if (joursEcoules > joursInactivite) {
                        alertes.add("Compte " + idCompte + " : inactif depuis " + joursEcoules + " jours");
                    }
                } else {
                    alertes.add("Compte " + idCompte + " : jamais actif");
                }
            }
        }
        return alertes;
    }




}
