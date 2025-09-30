package entities;

public final class CompteCourant extends Compte{
    private double decouvert;

    public CompteCourant(long id, String numero, double solde, long idClient, double decouvert) {
        super(id, numero, solde, idClient);
        this.decouvert = decouvert;
    }

    public double getDecouvert() {
        return decouvert;
    }

    public void setDecouvert(double decouvert) {
        this.decouvert = decouvert;
    }

    @Override
    public String toString() {
        return "CompteCourant{" +
                "decouvert=" + decouvert +
                ", idClient=" + idClient +
                ", solde=" + solde +
                ", numero='" + numero + '\'' +
                ", id=" + id +
                '}';
    }
}
