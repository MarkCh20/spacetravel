package com.spacetravel.service;

import com.spacetravel.entity.Ticket;
import java.time.LocalDate;
import java.util.List;

public interface TicketCrudService {
    Ticket create(Ticket ticket);
    Ticket findById(Long id);
    List<Ticket> findAll();
    List<Ticket> findAllByClient(Long clientId);
    List<Ticket> findAllByFromPlanet(String planetId);
    List<Ticket> findAllByToPlanet(String planetId);
    List<Ticket> findAllByDate(LocalDate createdAt);
    Ticket updateFromPlanet(Long id, String fromPlanetId);
    Ticket updateToPlanet(Long id, String toPlanetId);
    void delete(Long id);
    void deleteAllByClientId(Long clientId);
    void deleteAllByFromPlanetId(String fromPlanetId);
    void deleteAllByToPlanetId(String toPlanetId);
}
