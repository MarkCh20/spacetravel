package com.spacetravel.dao;

import com.spacetravel.entity.Planet;
import java.util.List;
import java.util.Optional;

public interface PlanetDao {
    Planet save(Planet planet);
    Optional<Planet> findById(String id);
    Optional<Planet> findByName(String name);
    List<Planet> findAll();
    void delete(Planet planet);
    Planet update(Planet planet);
}
