package entities;

import java.time.LocalDateTime;


public record Transaction(long id, LocalDateTime date,
                          double montant,
                          TransactionType type,
                          String lieu,
                          long idCompte) {
}
