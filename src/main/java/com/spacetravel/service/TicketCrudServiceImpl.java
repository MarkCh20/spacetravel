package com.spacetravel.service;

import com.spacetravel.dao.PlanetDao;
import com.spacetravel.dao.TicketDao;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.exception.TicketNotFoundException;
import com.spacetravel.util.LoggerUtil;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class TicketCrudServiceImpl implements TicketCrudService {

    private final TicketDao ticketDao;
    private final PlanetDao planetDao;

    private final Logger logger = LoggerUtil.getLogger(TicketCrudServiceImpl.class);

    public TicketCrudServiceImpl(TicketDao ticketDao, PlanetDao planetDao) {
        this.ticketDao = ticketDao;
        this.planetDao = planetDao;
    }

    public Ticket create(Ticket ticket) {
        validateData(ticket);
        logger.info("Creating ticket: {}", ticket.getId());
        return ticketDao.save(ticket);
    }

    public Ticket findById(Long id) {
        validateId(id);
        return ticketDao.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    public List<Ticket> findAll() {
        return ticketDao.findAll();
    }

    public List<Ticket> findAllByClient(Long clientId) {
        return ticketDao.findAllByClient(clientId);
    }

    public List<Ticket> findAllByFromPlanet(String planetId) {
        return ticketDao.findAllByFromPlanet(planetId);
    }

    public List<Ticket> findAllByToPlanet(String planetId) {
        return ticketDao.findAllByToPlanet(planetId);
    }

    public List<Ticket> findAllByDate(LocalDate createdAt) {
        return ticketDao.findAllByDate(createdAt);
    }

    public Ticket updateFromPlanet(Long id, String fromPlanetId) {
        validateId(id);
        Planet fromPlanet = planetDao.findById(fromPlanetId)
                .orElseThrow(() -> new PlanetNotFoundException("From planet not found with id: ", fromPlanetId));

        ticketDao.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Attempted to update nonexistent ticket with id: ", id));

        logger.info("Updating fromPlanet of ticket {} to '{}'", id, fromPlanetId);
        return ticketDao.updateFromPlanet(id, fromPlanet);

    }

    public Ticket updateToPlanet(Long id, String toPlanetId) {
        validateId(id);
        Planet toPlanet = planetDao.findById(toPlanetId)
                .orElseThrow(() -> new PlanetNotFoundException("To planet not found with id: ", toPlanetId));

        ticketDao.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Attempted to update nonexistent ticket with id: ", id));

        logger.info("Updating toPlanet of ticket {} to '{}'", id, toPlanetId);
        return ticketDao.updateToPlanet(id, toPlanet);
    }

    public void delete(Long id) {
        validateId(id);
        ticketDao.findById(id)
                .ifPresentOrElse(
                        ticket -> {
                            logger.info("Deleting ticket with ID: {}", id);
                            ticketDao.delete(ticket);
                        },
                        () -> {
                            logger.warn("Attempted to delete nonexistent ticket: {}", id);
                            throw new TicketNotFoundException("Attempted to delete nonexistent ticket with id: ", id);
                        }
                );

    }

    public void deleteAllByClientId(Long clientId) {
        validateId(clientId);
        List<Ticket> tickets = ticketDao.findAllByClient(clientId);
        if (tickets.isEmpty()) {
            logger.warn("Attempted to delete tickets for nonexistent clientId: {}", clientId);
            throw new TicketNotFoundException("No tickets found for clientId: ", clientId);
        }

        tickets.forEach(ticket -> {
            logger.info("Deleting ticket {} for clientId={}", ticket.getId(), clientId);
            ticketDao.delete(ticket);
        });
    }

    public void deleteAllByFromPlanetId(String fromPlanetId) {
        validatePlanetId(fromPlanetId);
        List<Ticket> tickets = ticketDao.findAllByFromPlanet(fromPlanetId);
        if (tickets.isEmpty()) {
            logger.warn("Attempted to delete tickets for nonexistent fromPlanetId: {}", fromPlanetId);
            throw new TicketNotFoundException("No tickets found for fromPlanetId: ", fromPlanetId);
        }

        tickets.forEach(ticket -> {
            logger.info("Deleting ticket {} from fromPlanetId={}", ticket.getId(), fromPlanetId);
            ticketDao.delete(ticket);
        });
    }

    public void deleteAllByToPlanetId(String toPlanetId) {
        validatePlanetId(toPlanetId);
        List<Ticket> tickets = ticketDao.findAllByToPlanet(toPlanetId);
        if (tickets.isEmpty()) {
            logger.warn("Attempted to delete tickets for nonexistent toPlanetId: {}", toPlanetId);
            throw new TicketNotFoundException("No tickets found for toPlanetId: ", toPlanetId);
        }

        tickets.forEach(ticket -> {
            logger.info("Deleting ticket {} to toPlanetId={}", ticket.getId(), toPlanetId);
            ticketDao.delete(ticket);
        });
    }

    private void validateData(Ticket ticket) {
        if (ticket.getClient() == null || ticket.getFromPlanet() == null || ticket.getToPlanet() == null) {
            throw new IllegalArgumentException("Client, fromPlanet and toPlanet must not be null");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Client ID must be a positive number");
        }
    }

    private void validatePlanetId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Planet id must not be empty");
        }
    }

}