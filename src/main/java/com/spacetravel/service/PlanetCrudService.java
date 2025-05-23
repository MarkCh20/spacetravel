package com.spacetravel.service;

import com.spacetravel.entity.Planet;
import java.util.List;

public interface PlanetCrudService {
    Planet create(String id, String name);
    Planet findById(String id);
    List<Planet> findAll();
    Planet update(String id, String name);
    void delete(String id);
}
