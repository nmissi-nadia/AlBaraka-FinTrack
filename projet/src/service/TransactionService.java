package service;

import dao.TransactionDao;
import entities.Transaction;
import java.util.List;

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

    public List<Transaction> listerTransactionsParCompte(int compteId) {
        return transactionDao.findByCompteId(compteId);
    }
    public List<String> transactionsParTypeEtMois() {
        return transactionDao.transactionsParTypeEtMois();
    }
    public List<String> top5Clients() {
        return transactionDao.top5Clients();
    }
    public List<String> transactionsSuspectes() {
        return transactionDao.transactionsSuspectes();
    }


}
