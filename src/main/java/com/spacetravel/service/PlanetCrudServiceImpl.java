package com.spacetravel.service;

import com.spacetravel.dao.PlanetDaoImpl;
import com.spacetravel.dao.TicketDaoImpl;
import com.spacetravel.entity.Planet;
import com.spacetravel.exception.DuplicatePlanetIdException;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class PlanetCrudServiceImpl implements PlanetCrudService {

    private final PlanetDaoImpl planetDao;
    private final TicketDaoImpl ticketDao;
    private final Logger logger = LoggerUtil.getLogger(PlanetCrudServiceImpl.class);

    public PlanetCrudServiceImpl(PlanetDaoImpl planetDao, TicketDaoImpl ticketDao) {
        this.planetDao = planetDao;
        this.ticketDao = ticketDao;
    }

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

    public Planet findById(String id) {
        validateId(id);
        return planetDao.findById(id)
                .orElseThrow(() -> PlanetNotFoundException.forId(id));
    }

    public Planet findByName(String name) {
        validateName(name);
        return planetDao.findByName(name)
                .orElseThrow(() -> PlanetNotFoundException.forName(name));
    }

    public Optional<Planet> findOptionalByName(String name) {
        validateName(name);
        return planetDao.findByName(name);
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
                .orElseThrow(() -> new PlanetNotFoundException("Attempted to update nonexistent planet with id: ", id));
    }

    public void delete(String id) {
        validateId(id);
        planetDao.findById(id)
                .ifPresentOrElse(
                        planet -> {
                            logger.info("Deleting planet with ID: {}", id);

                            ticketDao.deleteAllByFromPlanetId(id);
                            ticketDao.deleteAllByToPlanetId(id);

                            planetDao.delete(planet);
                        },
                        () -> {
                            logger.warn("Attempted to delete nonexistent planet: {}", id);
                            throw new PlanetNotFoundException("Attempted to delete nonexistent planet with id: ", id);
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
