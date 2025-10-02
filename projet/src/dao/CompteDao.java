package dao;

import entities.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompteDao implements GenInDao<Compte> {
    private final Connection connection;

    public CompteDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Compte compte) {
        String sql = "INSERT INTO comptes (numero_compte, solde, client_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getNumero());
            stmt.setDouble(2, compte.getSolde());
            stmt.setLong(3, compte.getIdClient());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Compte findById(int id) {
        String sql = "SELECT * FROM compte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String type = rs.getString("type_compte");
                if ("COURANT".equalsIgnoreCase(type)) {
                    return new CompteCourant(
                            rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getInt("id_client"),
                            rs.getDouble("decouvert_autorise")
                    );
                } else if ("EPARGNE".equalsIgnoreCase(type)) {
                    return new CompteEpargne(
                            rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getInt("id_client"),
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
                            rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getInt("id_client"),
                            rs.getDouble("decouvert_autorise")
                    ));
                } else if ("EPARGNE".equalsIgnoreCase(type)) {
                    comptes.add(new CompteEpargne(
                            rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getDouble("solde"),
                            rs.getInt("id_client"),
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
        String sql = "UPDATE compte SET numero_compte=?, solde=?, client_id=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, compte.getNumero());
            stmt.setDouble(2, compte.getSolde());
            stmt.setLong(3, compte.getIdClient());
            stmt.setLong(4, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Compte compte) {
        String sql = "DELETE FROM compte WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private Compte mapResultSetToCompte(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String numero = rs.getString("numero_compte");
        double solde = rs.getDouble("solde");
        int clientId = rs.getInt("client_id");
        String typeCompte = rs.getString("type_compte");

        // Si c'est un compte courant
        if ("COURANT".equalsIgnoreCase(typeCompte)) {
            double decouvert = rs.getDouble("decouvert_autorise");
            return new CompteCourant(id, numero, solde, clientId, decouvert);
        }

        // Si c'est un compte épargne
        else if ("EPARGNE".equalsIgnoreCase(typeCompte)) {
            double tauxInteret = rs.getDouble("taux_interet");
            return new CompteEpargne(id, numero, solde, clientId, tauxInteret);
        }

        throw new SQLException("Type de compte inconnu : " + typeCompte);
    }
    // dao/CompteDao.java
    public List<String> comptesInactifs() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT cp.numero, c.nom, MAX(t.date) as derniere_tx
        FROM compte cp
        JOIN client c ON cp.id_client = c.id
        LEFT JOIN transactions t ON cp.id = t.id_compte
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
    // dao/CompteDao.java
    public List<String> verifierAlertes() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT numero, solde
        FROM compte
        WHERE solde < 0
           OR solde < 100
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add("Compte: " + rs.getString("numero") +
                        " | Solde: " + rs.getDouble("solde"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



}
