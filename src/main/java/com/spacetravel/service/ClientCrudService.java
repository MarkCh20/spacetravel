package com.spacetravel.service;

import com.spacetravel.entity.Client;
import java.util.List;

public interface ClientCrudService {
    Client create(String name);
    Client findById(Long id);
    List<Client> findAll();
    Client update(Long id, String name);
    void delete(Long id);
}
