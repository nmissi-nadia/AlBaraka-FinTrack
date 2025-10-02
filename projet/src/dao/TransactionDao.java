package dao;

import entities.*;
import utilitaire.exceptions.Database;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements GenInDao<Transaction> {
    private final Connection connection;

    public TransactionDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Transaction transaction) {
        String sql = "INSERT INTO transactions (montant, type_tx, lieu, id_compte) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.montant());
            stmt.setString(2, transaction.type().name()); // Enum en String
            stmt.setString(3, transaction.lieu());
            stmt.setLong(4, transaction.idCompte());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Transaction findById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET montant=?, type_tx=?, lieu=?, id_compte=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.montant());
            stmt.setString(2, transaction.type().name());
            stmt.setString(3, transaction.lieu());
            stmt.setLong(4, transaction.idCompte());
            stmt.setLong(5, transaction.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Transaction transaction) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, transaction.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> findByCompteId(long compteId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE id_compte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, compteId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getLong("id"),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getDouble("montant"),
                TransactionType.valueOf(rs.getString("type_tx")), // convertit VARCHAR → Enum
                rs.getString("lieu"),
                rs.getLong("id_compte")
        );
    }
    // dao/TransactionDao.java
    public List<String> top5Clients() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT c.nom, SUM(t.montant) as total
        FROM client c
        JOIN compte cp ON c.id = cp.id_client
        JOIN transactions t ON cp.id = t.id_compte
        GROUP BY c.nom
        ORDER BY total DESC
        LIMIT 5
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add("Client: " + rs.getString("nom") + " | Total: " + rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> transactionsParTypeEtMois() {
        List<String> result = new ArrayList<>();
        String sql = """
        SELECT type_tx, EXTRACT(MONTH FROM date) as mois, SUM(montant) as total
        FROM transactions
        GROUP BY type_tx, mois
        ORDER BY mois, type_tx
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add("Mois: " + rs.getInt("mois") +
                        " | Type: " + rs.getString("type_tx") +
                        " | Total: " + rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> transactionsSuspectes() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT id, montant, type_tx, date FROM transactions WHERE montant > 10000";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add("Tx ID: " + rs.getLong("id") +
                        " | Montant: " + rs.getDouble("montant") +
                        " | Type: " + rs.getString("type_tx") +
                        " | Date: " + rs.getTimestamp("date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



}
