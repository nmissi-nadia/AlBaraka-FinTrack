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


    @Override
    public void update(Compte compte) {
        String sql = "UPDATE comptes SET numero_compte=?, solde=?, client_id=? WHERE id=?";
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
        String sql = "DELETE FROM comptes WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, compte.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
