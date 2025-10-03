package entities;

import java.util.UUID;

public record Client(UUID id, String nom, String email ) {
    @Override
    public String toString() {
        return "Client[id=" + id() + ", nom=" + nom() + ", email=" + email() + "]";
    }
}
