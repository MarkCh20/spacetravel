package com.spacetravel.service;

import com.spacetravel.dao.PlanetDao;
import com.spacetravel.entity.Planet;
import com.spacetravel.exception.DuplicatePlanetIdException;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class PlanetCrudService {

    private final PlanetDao planetDao = new PlanetDao();
    private final Logger logger = LoggerUtil.getLogger(PlanetCrudService.class);

    public Planet create(String id, String name) {
        validateId(id);
        validateName(name);
        if (planetDao.findById(id).isPresent()) {
            throw new DuplicatePlanetIdException("Planet with ID '" + id + "' already exists.");
        }
        Planet planet = new Planet(id, name);
        logger.info("Creating planet: {} - {}", id, name);
        return planetDao.save(planet);
    }

    public Optional<Planet> findById(String id) {
        validateId(id);
        return planetDao.findById(id);
    }

    public List<Planet> findAll() {
        return planetDao.findAll();
    }

    public Planet update(String id, String name) {
        validateId(id);
        validateName(name);
        return planetDao.findById(id)
                .map(planet -> {
                    planet.setName(name);
                    logger.info("Updating planet {} to new name '{}'", id, name);
                    return planetDao.update(planet);
                })
                .orElseThrow(() -> new PlanetNotFoundException(id));
    }

    public void delete(String id) {
        validateId(id);
        planetDao.findById(id)
                .ifPresentOrElse(
                        planet -> {
                            logger.info("Deleting planet with ID: {}", id);
                            planetDao.delete(planet);
                        },
                        () -> {
                            logger.warn("Attempted to delete nonexistent planet: {}", id);
                            throw new PlanetNotFoundException(id);
                        }
                );
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Planet name must not be empty");
        }
        if (name.length() > 500) {
            throw new IllegalArgumentException("Planet name must be not longer than 500 characters");
        }
    }

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Planet ID must not be empty");
        }
    }
}
