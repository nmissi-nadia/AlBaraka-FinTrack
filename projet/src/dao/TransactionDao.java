package dao;

import entities.*;
import java.sql.*;
import java.util.*;
import utilitaire.exceptions.Database;

public class TransactionDao implements GenInDao<Transaction> {

    private final Connection connection;

    public TransactionDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Transaction transaction) {
        // Vérifier que les comptes source/destination existent
        if (!compteExiste(transaction.compteSource())) {
            System.err.println("Le compte source n'existe pas : " + transaction.compteSource());
            return;
        }
        if (transaction.compteDestination() != null && !compteExiste(transaction.compteDestination())) {
            System.err.println("Le compte destination n'existe pas : " + transaction.compteDestination());
            return;
        }

        String sql = """
            INSERT INTO transactions (montant, type_tx, lieu, compte_source, compte_destination)
            VALUES (?, ?, ?, ?, ?)
        """;

        // Connexion déjà injectée, mais on garde le try-with-resources pour PreparedStatement
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.montant());
            stmt.setString(2, transaction.type().name());
            stmt.setString(3, transaction.lieu());
            stmt.setObject(4, transaction.compteSource());
            stmt.setObject(5, transaction.compteDestination());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifie si un compte existe
    private boolean compteExiste(UUID compteId) {
        String sql = "SELECT 1 FROM compte WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, compteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Transaction findById(UUID id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
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
        String sql = """
            UPDATE transactions
            SET montant=?, type_tx=?, lieu=?, compte_source=?, compte_destination=?
            WHERE id=?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.montant());
            stmt.setString(2, transaction.type().name());
            stmt.setString(3, transaction.lieu());
            stmt.setObject(4, transaction.compteSource());
            stmt.setObject(5, transaction.compteDestination());
            stmt.setObject(6, transaction.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Transaction transaction) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, transaction.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> findByCompteId(UUID compteId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE compte_source = ? OR compte_destination = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, compteId);
            stmt.setObject(2, compteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getObject("id", UUID.class),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getDouble("montant"),
                TransactionType.valueOf(rs.getString("type_tx")),
                rs.getString("lieu"),
                rs.getObject("compte_source", UUID.class),
                rs.getObject("compte_destination", UUID.class)
        );
    }

    public List<String> top5Clients() {
        List<String> result = new ArrayList<>();
        String sql = """
            SELECT c.nom, SUM(t.montant) as total
            FROM client c
            JOIN compte cp ON c.id = cp.id_client
            JOIN transactions t ON (cp.id = t.compte_source OR cp.id = t.compte_destination)
            GROUP BY c.nom
            ORDER BY total DESC
            LIMIT 5
        """;

        // Connexion locale pour éviter la fermeture accidentelle
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add("Client: " + rs.getString("nom") + " | Total: " + rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Map<String, Double> getRapportMensuel(int mois, int annee) {
        Map<String, Double> rapport = new HashMap<>();
        String sql = """
            SELECT type_tx, SUM(montant) as total
            FROM transactions
            WHERE EXTRACT(MONTH FROM date) = ?
            AND EXTRACT(YEAR FROM date) = ?
            GROUP BY type_tx
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mois);
            stmt.setInt(2, annee);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rapport.put(rs.getString("type_tx"), rs.getDouble("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rapport;
    }

    public List<String> transactionsParTypeEtMois() {
        List<String> result = new ArrayList<>();
        String sql = """
            SELECT type_tx, EXTRACT(MONTH FROM date) as mois, SUM(montant) as total
            FROM transactions
            GROUP BY type_tx, mois
            ORDER BY mois, type_tx
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
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

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add("Tx ID: " + rs.getObject("id") +
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
