package service;

import dao.CompteDao;
import entities.*;
import utilitaire.exceptions.CompteNotFoundException;

import java.util.List;

public class CompteService {
    private final CompteDao compteDao;

    public CompteService(CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    public void creerCompteCourant(String numero, double solde, int clientId, double decouvertAutorise) {
        CompteCourant cc = new CompteCourant(0, numero, solde, clientId, decouvertAutorise);
        compteDao.create(cc);
    }

    public void creerCompteEpargne(String numero, double solde, int clientId, double tauxInteret) {
        CompteEpargne ce = new CompteEpargne(0, numero, solde, clientId, tauxInteret);
        compteDao.create(ce);
    }

    public Compte rechercherCompteParId(int id) {
        Compte compte = compteDao.findById(id);
        if (compte == null) {
            throw new CompteNotFoundException("Compte avec id " + id + " introuvable.");
        }
        return compte;
    }

    public List<Compte> rechercherComptesParClient(int clientId) {
        return compteDao.findByClientId(clientId); // doit retourner List<Compte>
    }


    public void mettreAJourCompte(Compte compte) {
        compteDao.update(compte);
    }

    public void supprimerCompte(Compte id) {
        compteDao.delete(id);
    }

    public Compte compteAvecSoldeMax() {
        return compteDao.findAll().stream()
                .max((c1, c2) -> Double.compare(c1.getSolde(), c2.getSolde()))
                .orElseThrow(() -> new CompteNotFoundException("Aucun compte trouvé."));
    }

    public Compte compteAvecSoldeMin() {
        return compteDao.findAll().stream()
                .min((c1, c2) -> Double.compare(c1.getSolde(), c2.getSolde()))
                .orElseThrow(() -> new CompteNotFoundException("Aucun compte trouvé."));
    }
    public List<Compte> listerComptes() {
        return compteDao.findAll();
    }
    public List<String> comptesInactifs() {
        return compteDao.comptesInactifs();
    }
    public List<String> verifierAlertes() {
        return compteDao.verifierAlertes();
    }



}
