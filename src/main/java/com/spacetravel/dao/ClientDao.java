package com.spacetravel.dao;

import com.spacetravel.entity.Client;
import java.util.List;
import java.util.Optional;

public interface ClientDao {
    Client save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    void delete(Client client);
    Client update(Client client);
}
