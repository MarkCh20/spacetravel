package com.spacetravel.service;

import com.spacetravel.entity.Planet;
import java.util.List;
import java.util.Optional;

public interface PlanetCrudService {
    Planet create(String id, String name);
    Planet findById(String id);
    Planet findByName(String name);
    Optional<Planet> findOptionalByName(String name);
    List<Planet> findAll();
    Planet update(String id, String name);
    void delete(String id);
}
