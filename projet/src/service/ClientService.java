package service;

import dao.ClientDao;
import entities.Client;
import utilitaire.exceptions.ClientNotFoundException;

import java.util.List;
import java.util.UUID;

public class ClientService {
    private final ClientDao clientDao;

    public ClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public void ajouterClient(Client client) {
        clientDao.create(client);
    }

    public void modifierClient(Client client) {
        clientDao.update(client);
    }

    public void supprimerClient(Client id) {
        clientDao.delete(id);
    }

    public Client rechercherClientParId(UUID id) {
        Client client = clientDao.findById(id);
        if (client == null) {
            throw new ClientNotFoundException("Client avec id " + id + " introuvable.");
        }
        return client;
    }

    public List<Client> rechercherTousLesClients() {
        return clientDao.findAll();
    }
    public List<Client> listerClients() {
        return clientDao.findAll();
    }
    public int compterClients() {
        return clientDao.findAll().size();
    }
}
