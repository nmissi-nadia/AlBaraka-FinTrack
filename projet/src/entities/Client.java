package entities;

public record Client(long id,String nom, String email ) {
    @Override
    public String toString() {
        return "Client[id=" + id() + ", nom=" + nom() + "]";
    }
}
