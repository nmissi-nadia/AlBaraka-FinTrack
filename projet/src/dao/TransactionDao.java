package dao;

import entities.Transaction;

import java.sql.Connection;
import java.util.List;

public class TransactionDao implements GenInDao<Transaction> {
    private final Connection con;
    public TransactionDao(Connection con) {
        this.con = con;
    }
    @Override
    public void create(Transaction entity) {}
    @Override
    public void update(Transaction entity) {}
    @Override
    public void delete(Transaction entity) {}


    @Override
    public Transaction findById(int id) {
        return null;
    }

    @Override
    public List<Transaction> findAll() {
        return List.of();
    }
}
