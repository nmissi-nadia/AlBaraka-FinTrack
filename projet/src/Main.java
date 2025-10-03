import dao.*;
import service.*;
import entities.*;
import utilitaire.exceptions.Database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        try (Connection conn = Database.getConnection()) {

            // DAO et Services
            ClientDao clientDao = new ClientDao(conn);
            CompteDao compteDao = new CompteDao(conn);
            TransactionDao transactionDao = new TransactionDao(conn);

            ClientService clientService = new ClientService(clientDao);
            CompteService compteService = new CompteService(compteDao);
            TransactionService transactionService = new TransactionService(transactionDao);
            RapportService rapportService =new RapportService(clientDao,compteDao,transactionDao);

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n===== MENU PRINCIPAL BANCAIRE =====");
                System.out.println("1. Gestion Clients & Comptes");
                System.out.println("2. Gestion Transactions");
                System.out.println("3. Rapports & Analyses");
                System.out.println("0. Quitter");
                System.out.print("Votre choix : ");
                int choix = scanner.nextInt();
                scanner.nextLine(); // consommer retour à la ligne

                switch (choix) {
                    case 1 -> menuClientsComptes(scanner, clientService, compteService);
                    case 2 -> menuTransactionsAnalyses(scanner, transactionService);
                    case 3 -> menuRapports(scanner,rapportService, transactionService, compteService);
                    case 0 -> {
                        running = false;
                        System.out.println("Fermeture du programme. Au revoir !");
                    }
                    default -> System.out.println("Choix invalide, veuillez réessayer.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void menuClientsComptes(Scanner scanner, ClientService clientService, CompteService compteService) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion Clients & Comptes ---");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Lister les clients");
            System.out.println("3. Modifier un client");
            System.out.println("4. Supprimer un client");
            System.out.println("5. Créer un compte");
            System.out.println("6. Lister les comptes");
            System.out.println("7. Modifier un compte");
            System.out.println("8. Supprimer un compte");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                // Ajouter un client
                case 1 -> {
                    System.out.print("Nom du client: ");
                    String nom = scanner.nextLine();
                    System.out.print("Email du client: ");
                    String email = scanner.nextLine();
                    clientService.ajouterClient(new Client(null, nom, email));
                    System.out.println("Client ajouté avec succès.");
                }

                case 2 -> {
                    List<Client> clients = clientService.listerClients();
                    if (clients.isEmpty()) System.out.println("Aucun client trouvé.");
                    else clients.forEach(System.out::println);
                }

                case 3 -> {
                    System.out.print("ID du client à modifier: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    scanner.nextLine();
                    System.out.print("Nouveau nom: ");
                    String newNom = scanner.nextLine();
                    System.out.print("Nouvel email: ");
                    String newEmail = scanner.nextLine();
                    clientService.modifierClient(new Client(id, newNom, newEmail));
                    System.out.println("Client modifié avec succès.");
                }

                case 4 -> {
                    System.out.print("ID du client à supprimer: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    Client cl=clientService.rechercherClientParId(id);
                    clientService.supprimerClient(cl);
                    System.out.println("Client supprimé avec succès.");
                }

                case 5 -> {
                    System.out.print("ID du client: ");
                    UUID clientId = UUID.fromString(scanner.nextLine());
                    scanner.nextLine();
                    System.out.print("Numéro de compte: ");
                    String numero = scanner.nextLine();
                    System.out.print("Solde initial: ");
                    double solde = scanner.nextDouble();
                    System.out.print("Type de compte (1 = Courant, 2 = Épargne): ");
                    int typeCompte = scanner.nextInt();
                    if (typeCompte == 1) {
                        System.out.print("Découvert autorisé: ");
                        double decouvert = scanner.nextDouble();
                        compteService.creerCompteCourant(numero, solde, clientId, decouvert);
                    } else {
                        System.out.print("Taux d’intérêt (%): ");
                        double taux = scanner.nextDouble();
                        compteService.creerCompteEpargne(numero, solde, clientId, taux);
                    }
                    System.out.println("Compte créé avec succès.");
                }

                // Lister comptes
                case 6 -> {
                    List<Compte> comptes = compteService.listerComptes();
                    if (comptes.isEmpty()) System.out.println("Aucun compte trouvé.");
                    else comptes.forEach(System.out::println);
                }

                // Modifier un compte
                case 7 -> {
                    System.out.print("ID du compte à modifier: ");
                    UUID id = UUID.fromString(scanner.nextLine());

                    scanner.nextLine();

                    System.out.print("Nouveau numéro: ");
                    String newNumero = scanner.nextLine();

                    System.out.print("Nouveau solde: ");
                    double newSolde = scanner.nextDouble();

                    System.out.print("Type de compte (1 = Courant, 2 = Épargne): ");
                    int typeCompte = scanner.nextInt();

                    Compte compte;
                    if (typeCompte == 1) {
                        System.out.print("Nouveau découvert autorisé: ");
                        double decouvert = scanner.nextDouble();
                        compte = new CompteCourant(id, newNumero, newSolde, null, decouvert);
                    } else {
                        System.out.print("Nouveau taux d’intérêt: ");
                        double taux = scanner.nextDouble();
                        compte = new CompteEpargne(id, newNumero, newSolde, null, taux);
                    }
                    compteService.mettreAJourCompte(compte);
                    System.out.println("Compte modifié avec succès.");

                }

                // Supprimer un compte
                case 8 -> {
                    System.out.print("ID du compte à supprimer: ");
                    UUID id = UUID.fromString(scanner.nextLine());
                    Compte compte = compteService.rechercherCompteParId(id);
                    compteService.supprimerCompte(compte);
                    System.out.println("Compte supprimé avec succès.");
                }

                case 0 -> back = true;

                default -> System.out.println("Choix invalide.");
            }
        }
    }


    private static void menuTransactionsAnalyses(Scanner scanner, TransactionService transactionService) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Gestion des Transactions ---");
            System.out.println("1. Effectuer une transaction");
            System.out.println("2. Historique d’un compte");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    System.out.print("ID du compte source: ");
                    UUID compteSource = UUID.fromString(scanner.nextLine());

                    UUID compteDestination = null;
                    System.out.print("Type (VERSEMENT/RETRAIT/VIREMENT): ");
                    TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());

                    if (type == TransactionType.VIREMENT) {
                        System.out.print("ID du compte destination: ");
                        compteDestination = UUID.fromString(scanner.nextLine());
                    }

                    System.out.print("Montant: ");
                    double montant = scanner.nextDouble();
                    scanner.nextLine();

                    System.out.print("Lieu: ");
                    String lieu = scanner.nextLine();

                    Transaction txSource = new Transaction(
                            UUID.randomUUID(),
                            LocalDateTime.now(),
                            montant,
                            type,
                            lieu,
                            compteSource,
                            compteDestination
                    );
                    transactionService.ajouterTransaction(txSource);

                    System.out.println("Transaction effectuée avec succès.");
                }


                case 2 -> {
                    System.out.print("ID du compte: ");
                    UUID compteId = UUID.fromString(scanner.nextLine());
                    List<Transaction> transactions = transactionService.listerTransactionsParCompte(compteId);
                    if (transactions.isEmpty()) {
                        System.out.println("Aucune transaction trouvée pour ce compte.");
                    } else {
                        transactions.forEach(System.out::println);
                    }
                }

                case 0 -> back = true;

                default -> System.out.println("Choix invalide, réessayez.");
            }
        }
    }

    private static void menuRapports(Scanner scanner,RapportService rapportService, TransactionService transactionService, CompteService compteService) throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Rapports & Analyses ---");
            System.out.println("1. Top 5 clients");
            System.out.println("2. Transactions par type/mois");
            System.out.println("3. Comptes inactifs");
            System.out.println("4. Transactions suspectes");
            System.out.println("5. Alertes comptes");
            System.out.println("6. Générer rapport complet");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> rapportService.top5Clients().forEach(System.out::println);
                case 2 -> transactionService.transactionsParTypeEtMois().forEach(System.out::println);
                case 3 -> rapportService.comptesInactifs().forEach(System.out::println);
                case 4 -> rapportService.transactionsSuspectes().forEach(System.out::println);
                case 5 -> rapportService.verifierAlertes().forEach(System.out::println);
                case 6 -> {
                    StringBuilder rapport = new StringBuilder();
                    rapport.append("=== RAPPORT COMPLET ===\n\n");

                    rapport.append("--- Top 5 Clients ---\n");
                    rapportService.top5Clients().forEach(c -> rapport.append(c).append("\n"));

                    rapport.append("\n--- Transactions par type/mois ---\n");
                    transactionService.transactionsParTypeEtMois().forEach(t -> rapport.append(t).append("\n"));

                    rapport.append("\n--- Comptes inactifs ---\n");
                    rapportService.comptesInactifs().forEach(c -> rapport.append(c).append("\n"));

                    rapport.append("\n--- Transactions suspectes ---\n");
                    rapportService.transactionsSuspectes().forEach(t -> rapport.append(t).append("\n"));

                    rapport.append("\n--- Alertes comptes ---\n");
                    rapportService.verifierAlertes().forEach(a -> rapport.append(a).append("\n"));

                    try (FileWriter writer = new FileWriter("rapport_transactions.txt")) {
                        writer.write(rapport.toString());
                        System.out.println("Rapport généré : rapport_transactions.txt");
                    } catch (IOException e) {
                        System.out.println("Erreur lors de l'écriture du rapport : " + e.getMessage());
                    }
                }

                case 0 -> back = true;
                default -> System.out.println("Choix invalide, réessayez.");
            }
        }
    }

}
