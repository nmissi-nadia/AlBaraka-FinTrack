import dao.*;
import service.*;
import entities.*;
import utilitaire.exceptions.Database;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

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

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1. Gestion des clients et comptes");
                System.out.println("2. Transactions et analyses");
                System.out.println("0. Quitter");
                System.out.print("Votre choix : ");
                int choix = scanner.nextInt();
                scanner.nextLine();

                switch (choix) {
                    case 1 -> menuClientsComptes(scanner, clientService, compteService);
                    case 2 -> menuTransactionsAnalyses(scanner, transactionService, compteService);
                    case 0 -> {
                        running = false;
                        System.out.println("Fermeture du programme. Au revoir.");
                    }
                    default -> System.out.println("Choix invalide, réessayez.");
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
            System.out.println("3. Créer un compte");
            System.out.println("4. Lister les comptes");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    System.out.print("Nom du client: ");
                    String nom = scanner.nextLine();
                    System.out.print("Email du client: ");
                    String email = scanner.nextLine();
                    clientService.ajouterClient(new Client(0, nom, email));
                    System.out.println("Client ajouté avec succès.");
                }
                case 2 -> {
                    List<Client> clients = clientService.listerClients();
                    clients.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("ID du client: ");
                    int clientId = scanner.nextInt();
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
                case 4 -> compteService.listerComptes().forEach(System.out::println);
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ===== MENU TRANSACTIONS & ANALYSES =====
    private static void menuTransactionsAnalyses(Scanner scanner, TransactionService transactionService, CompteService compteService) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Transactions & Analyses ---");
            System.out.println("1. Effectuer une transaction");
            System.out.println("2. Historique d’un compte");
            System.out.println("3. Top 5 clients");
            System.out.println("4. Transactions par type/mois");
            System.out.println("5. Comptes inactifs");
            System.out.println("6. Transactions suspectes");
            System.out.println("7. Alertes comptes");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");
            int choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    System.out.print("ID du compte: ");
                    long compteId = scanner.nextLong();
                    System.out.print("Montant: ");
                    double montant = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Type (VERSEMENT/RETRAIT/VIREMENT): ");
                    String typeStr = scanner.nextLine();
                    System.out.print("Lieu: ");
                    String lieu = scanner.nextLine();
                    Transaction tx = new Transaction(
                            0,
                            LocalDateTime.now(),
                            montant,
                            TransactionType.valueOf(typeStr.toUpperCase()),
                            lieu,
                            compteId
                    );
                    transactionService.ajouterTransaction(tx);
                    System.out.println("Transaction effectuée.");
                }
                case 2 -> {
                    System.out.print("ID du compte: ");
                    int compteId = scanner.nextInt();
                    transactionService.listerTransactionsParCompte(compteId).forEach(System.out::println);
                }
                case 3 -> transactionService.top5Clients().forEach(System.out::println);
                case 4 -> transactionService.transactionsParTypeEtMois().forEach(System.out::println);
                case 5 -> compteService.comptesInactifs().forEach(System.out::println);
                case 6 -> transactionService.transactionsSuspectes().forEach(System.out::println);
                case 7 -> compteService.verifierAlertes().forEach(System.out::println);
                case 0 -> back = true;
                default -> System.out.println("Choix invalide.");
            }
        }
    }
}
