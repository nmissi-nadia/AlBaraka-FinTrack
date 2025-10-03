package service;

import dao.TransactionDao;
import entities.Transaction;

import java.util.List;
import java.util.UUID;

public class TransactionService {
    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public void ajouterTransaction(Transaction transaction) {
        transactionDao.create(transaction);
    }

    public List<Transaction> listerTransactions() {
        return transactionDao.findAll();
    }

    public List<Transaction> listerTransactionsParCompte(UUID compteId) {
        return transactionDao.findByCompteId(compteId);
    }

    public List<String> transactionsParTypeEtMois() {
        return transactionDao.transactionsParTypeEtMois();
    }



    public List<String> transactionsSuspectes() {
        return transactionDao.transactionsSuspectes();
    }
}
