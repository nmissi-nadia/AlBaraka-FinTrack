package entities;

import java.time.LocalDateTime;
import java.util.UUID;


public record Transaction(UUID id, LocalDateTime date,
                          double montant,
                          TransactionType type,
                          String lieu,
                          UUID compteSource,
                          UUID compteDestination
) {
}
