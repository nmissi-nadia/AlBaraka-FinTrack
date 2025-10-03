# AlBaraka-FinTrack
développement d'une application d'Analyse des Transactions Bancaires et Détection des Anomalies avec java 17, programmation fonctionnelle, persistance à la base de données

---

# Application de Gestion Bancaire

## Description
Cette application est une simulation d’un système bancaire développé en **Java** avec **PostgreSQL** comme base de données.  
Elle permet de gérer les clients, leurs comptes bancaires et les transactions associées, tout en fournissant des rapports et des analyses.

---

## Fonctionnalités principales
- Gestion des **clients** et de leurs **comptes** (création, consultation).
- **Transactions bancaires** : versement, retrait, virement.
- Historique des transactions par compte.
- Rapports analytiques :
  - Top 5 clients par solde.
  - Rapport mensuel (nombre et volume des transactions par type).
  - Détection des transactions suspectes.
  - Alertes sur les comptes (solde bas, inactivité prolongée).

---

## Conception

### Modèle conceptuel
L’application repose sur trois entités principales :  
- **Client** : représente un utilisateur de la banque.  
- **Compte** : associé à un client, avec un numéro et un solde.  
- **Transaction** : enregistre les mouvements financiers entre comptes.  
- **RapportService** : génère les rapports et alertes à partir des données.

### Diagramme de classes
```mermaid
classDiagram
    class Client {
        UUID id
        String nom
        String prenom
        String email
        +getComptes() List<Compte>
    }

    class Compte {
        UUID id_compte
        String numero
        double solde
        +crediter(double montant)
        +debiter(double montant)
    }

    class Transaction {
        UUID id
        LocalDateTime date_transaction
        double montant
        TransactionType type
        String lieu
        UUID compteSource
        UUID compteDestination
    }

    class RapportService {
        +getTopClients() List<Client>
        +getRapportMensuel() Map<TransactionType, Double>
        +getTransactionsSuspectes() List<Transaction>
        +getAlertesComptes() List<String>
    }

    Client "1" --> "*" Compte
    Compte "1" --> "*" Transaction : effectue
    RapportService ..> Transaction
    RapportService ..> Compte
    RapportService ..> Client
````

---

## Base de données

### Tables principales :

* **clients** (id, nom, prenom, email)
* **comptes** (id_compte, numero, solde, id_client)
* **transactions** (id, date_transaction, montant, type_tx, lieu, compte_source, compte_destination)

---

## Installation et exécution

1. **Cloner le projet**

   ```bash
   git clone https://github.com/nmissi-nadia/AlBaraka-FinTrack.git
   cd AlBaraka-FinTrack
   ```

2. **Configurer la base PostgreSQL**

   ```sql
   CREATE DATABASE banque;
   ```

3. **Mettre à jour la connexion** dans `Database.java`

   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/banque";
   private static final String USER = "postgres";
   private static final String PASSWORD = "votre_mot_de_passe";
   ```

4. **Compiler et exécuter**

   ```bash
   javac -d bin src/**/*.java
   java -cp bin Main
   ```

---

## Exemple d’utilisation

* Effectuer un virement entre deux comptes.
* Consulter l’historique des transactions d’un client.
* Générer un rapport mensuel des transactions.
* Afficher les alertes : comptes inactifs ou soldes bas.

---

##  Auteurs

* Projet développé dans le cadre d’un **atelier Java / PostgreSQL**.
* Auteur : *[Nmissi Nadia]*

---

