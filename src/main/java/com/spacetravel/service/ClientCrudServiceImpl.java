package com.spacetravel.service;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.dao.TicketDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.exception.ClientNotFoundException;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.List;

public class ClientCrudServiceImpl implements ClientCrudService {

    private final ClientDaoImpl clientDao;
    private final TicketDaoImpl ticketDao;
    private final Logger logger = LoggerUtil.getLogger(ClientCrudServiceImpl.class);

    public ClientCrudServiceImpl(ClientDaoImpl clientDao, TicketDaoImpl ticketDao) {
        this.clientDao = clientDao;
        this.ticketDao = ticketDao;
    }

    public Client create(String name) {
        validateName(name);
        Client client = new Client();
        client.setName(name);
        logger.info("Creating client: {}", name);
        return clientDao.save(client);
    }

    public Client findById(Long id) {
        validateId(id);
        return clientDao.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public List<Client> findAll() {
        return clientDao.findAll();
    }

    public Client update(Long id, String name) {
        validateId(id);
        validateName(name);
        return clientDao.findById(id)
                .map(client -> {
                    client.setName(name);
                    logger.info("Updating client {} to new name '{}'", id, name);
                    return clientDao.update(client);
                })
                .orElseThrow(() -> new ClientNotFoundException("Attempted to update nonexistent client with id: ", id));
    }

    public void delete(Long id) {
        validateId(id);
        clientDao.findById(id)
                .ifPresentOrElse(
                        client -> {
                            logger.info("Deleting client with ID: {}", id);

                            ticketDao.deleteAllByClientId(id);

                            clientDao.delete(client);
                        },
                        () -> {
                            logger.warn("Attempted to delete nonexistent client: {}", id);
                            throw new ClientNotFoundException("Attempted to delete nonexistent client with id: ", id);
                        }
                );
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Client name cannot be null");
        }
        name = name.trim();
        if (name.length() < 3) {
            throw new IllegalArgumentException("Client name must be at least 3 characters long");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Client name must be not longer than 200 characters");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Client ID must be a positive number");
        }
    }
}
