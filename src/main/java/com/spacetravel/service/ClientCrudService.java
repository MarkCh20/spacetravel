package com.spacetravel.service;

import com.spacetravel.dao.ClientDao;
import com.spacetravel.entity.Client;
import com.spacetravel.exception.ClientNotFoundException;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class ClientCrudService {

    private final ClientDao clientDao = new ClientDao();
    private final Logger logger = LoggerUtil.getLogger(ClientCrudService.class);

    public Client create(String name) {
        validateName(name);
        Client client = new Client();
        client.setName(name);
        logger.info("Creating client: {}", name);
        return clientDao.save(client);
    }

    public Optional<Client> findById(Long id) {
        validateId(id);
        return clientDao.findById(id);
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
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public void delete(Long id) {
        validateId(id);
        clientDao.findById(id)
                .ifPresentOrElse(
                        client -> {
                            logger.info("Deleting client with ID: {}", id);
                            clientDao.delete(client);
                        },
                        () -> {
                            logger.warn("Attempted to delete nonexistent client: {}", id);
                            throw new ClientNotFoundException(id);
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
