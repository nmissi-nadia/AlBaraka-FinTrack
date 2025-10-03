package entities;

import java.util.UUID;

public final class CompteEpargne extends Compte {
    private double tauxInteret;

    public CompteEpargne(UUID id, String numero, double solde, UUID idClient, double tauxInteret) {
        super(id, numero, solde, idClient);
        this.tauxInteret = tauxInteret;
    }
    public double getTauxInteret() {
        return tauxInteret;
    }
    public void setTauxInteret(double tauxInteret) {
        this.tauxInteret = tauxInteret;
    }

    @Override
    public String toString() {
        return "CompteEpargne{" +
                "tauxInteret=" + tauxInteret +
                ", id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                '}';
    }
}
