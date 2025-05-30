package com.spacetravel.dao;

import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TicketDao {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(Long id);
    List<Ticket> findAll();
    List<Ticket> findAllByClient(Long clientId);
    List<Ticket> findAllByFromPlanet(String planetId);
    List<Ticket> findAllByToPlanet(String planetId);
    List<Ticket> findAllByDate(LocalDate createdAt);
    Ticket updateFromPlanet(Long ticketId, Planet newFromPlanet);
    Ticket updateToPlanet(Long ticketId, Planet newToPlanet);
    void delete(Ticket ticket);
    void deleteAllByClientId(Long clientId);
    void deleteAllByFromPlanetId(String fromPlanetId);
    void deleteAllByToPlanetId(String toPlanetId);
}
