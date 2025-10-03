package service;

import dao.CompteDao;
import dao.TransactionDao;
import dao.ClientDao;
import entities.Client;
import entities.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RapportService {
    private final ClientDao clientDao;
    private final CompteDao compteDao;
    private final TransactionDao transactionDao;

    public RapportService(ClientDao clientDao, CompteDao compteDao, TransactionDao transactionDao) {
        this.clientDao = clientDao;
        this.compteDao = compteDao;
        this.transactionDao = transactionDao;
    }

    public List<String> top5Clients() {
        return transactionDao.top5Clients();
    }

    // 2. Rapport mensuel transactions par type
    public Map<String, Double> rapportMensuelTransactions(int mois, int annee) throws SQLException {
        return transactionDao.getRapportMensuel(mois, annee);
    }

    public List<String> transactionsSuspectes() {
        return transactionDao.transactionsSuspectes();
    }

    public List<String> comptesInactifs() {
        return compteDao.comptesInactifs();
    }
    public List<String> verifierAlertes() throws SQLException {
        return compteDao.getAlertesComptes();
    }
}
