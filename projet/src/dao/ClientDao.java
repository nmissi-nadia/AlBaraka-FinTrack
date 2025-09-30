package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entities.*;

public class ClientDao implements GenInDao<Client> {
    private final Connection connection;
    public ClientDao(Connection connection) {
        this.connection = connection;
    }
    //un nouveau client


    @Override
    public void create(Client cli) {
        String sql="INSERT INTO clients(nom ,email) VALUES (?,?,?)";
        try (PreparedStatement stmt =connection.prepareStatement(sql)){
            stmt.setString(1,cli.nom());
            stmt.setString(2,cli.email());
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public Client findById(int id) {
        String sql="SELECT * FROM clients WHERE id=?";
        try(PreparedStatement stmt =connection.prepareStatement(sql) ){
            stmt.setInt(1,id);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                rs.getInt("id");

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List findAll() {
        List<Client> clients = new ArrayList<>();
        String sql="SELECT * FROM clients";
        try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){

            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public void update(Client cl ) {
        String sql="UPDATE clients SET nom=?,email=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, cl.nom());
            stmt.setString(3, cl.email());
            stmt.setInt(4, (int) cl.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Client client) {
        String sql = "DELETE FROM clients WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, (int) client.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
