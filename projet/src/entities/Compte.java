package entities;

import java.util.UUID;

public sealed abstract class Compte permits CompteCourant,CompteEpargne {
    protected UUID id;
    protected String numero;
    protected double solde;
    protected UUID idClient;

    public Compte(UUID id, String numero, double solde, UUID idClient) {
        this.id = id;
        this.numero = numero;
        this.solde = solde;
        this.idClient = idClient;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UUID getIdClient() {
        return idClient;
    }

    public void setIdClient(UUID idClient) {
        this.idClient = idClient;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    @Override
    public String toString() {
        return "Compte{" +
                "id=" + id +
                ", numero='" + numero + '\'' +
                ", solde=" + solde +
                ", idClient=" + idClient +
                '}';
    }
}
