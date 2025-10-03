package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entities.*;

public class ClientDao implements GenInDao<Client> {

    private final Connection connection;

    public ClientDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(Client cli) {
        String sql = "INSERT INTO client(nom, email) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cli.nom());
            stmt.setString(2, cli.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Client findById(UUID id) {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Client(
                            rs.getObject("id", UUID.class),
                            rs.getString("nom"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT id, nom, email FROM client";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getObject("id", UUID.class),
                        rs.getString("nom"),
                        rs.getString("email")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    @Override
    public void update(Client cl) {
        String sql = "UPDATE client SET nom = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cl.nom());
            stmt.setString(2, cl.email());
            stmt.setObject(3, cl.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Client client) {
        String sql = "DELETE FROM client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, client.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
