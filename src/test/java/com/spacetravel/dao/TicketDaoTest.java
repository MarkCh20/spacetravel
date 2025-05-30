package com.spacetravel.dao;

import com.spacetravel.config.HibernateUtil;
import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketDaoTest {

    private static TicketDao ticketDao;
    private static ClientDao clientDao;
    private static PlanetDao planetDao;

    private Client savedClient;
    private Planet savedFromPlanet;
    private Planet savedToPlanet;

    @BeforeAll
    void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();
        flyway.migrate();

        ticketDao = new TicketDaoImpl();
        clientDao = new ClientDaoImpl();
        planetDao = new PlanetDaoImpl();
    }

    @BeforeEach
    void init() {
        Client client = new Client("Test Client");
        Planet from = new Planet("EAR1", "Earth");
        Planet to = new Planet("MRS1", "Mars");

        savedClient = clientDao.save(client);
        savedFromPlanet = planetDao.save(from);
        savedToPlanet = planetDao.save(to);
    }

    @Test
    @Order(1)
    void givenValidTicket_whenSave_thenTicketIsSavedWithId() {
        // Given
        Ticket ticket = new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now());


        // When
        Ticket saved = ticketDao.save(ticket);

        // Then
        assertNotNull(saved.getId());
        assertEquals(savedClient.getId(), saved.getClient().getId());
        assertEquals(savedFromPlanet.getId(), saved.getFromPlanet().getId());
        assertEquals(savedToPlanet.getId(), saved.getToPlanet().getId());
    }

    @Test
    @Order(2)
    void givenSavedTicket_whenFindById_thenReturnTicket() {
        // Given
        Ticket ticket = new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now());
        Ticket saved = ticketDao.save(ticket);

        // When
        Optional<Ticket> found = ticketDao.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @Order(3)
    void givenNoTickets_whenFindAll_thenReturnEmptyList() {
        // When
        List<Ticket> all = ticketDao.findAll();

        // Then
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    @Order(4)
    void givenClientId_whenFindAllByClient_thenReturnTicketsOfClient() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        List<Ticket> tickets = ticketDao.findAllByClient(savedClient.getId());

        // Then
        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t -> t.getClient().getId().equals(savedClient.getId())));
    }

    @Test
    @Order(5)
    void givenFromPlanetId_whenFindAllByFromPlanet_thenReturnTickets() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        List<Ticket> tickets = ticketDao.findAllByFromPlanet(savedFromPlanet.getId());

        // Then
        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t -> t.getFromPlanet().getId().equals(savedFromPlanet.getId())));
    }

    @Test
    @Order(6)
    void givenToPlanetId_whenFindAllByToPlanet_thenReturnTickets() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        List<Ticket> tickets = ticketDao.findAllByToPlanet(savedToPlanet.getId());

        // Then
        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t -> t.getToPlanet().getId().equals(savedToPlanet.getId())));
    }

    @Test
    @Order(7)
    void givenDate_whenFindAllByDate_thenReturnTickets() {
        // Given
        Instant now = Instant.now();
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, now));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, now));

        LocalDate date = now.atZone(ZoneId.systemDefault()).toLocalDate();

        // When
        List<Ticket> tickets = (ticketDao).findAllByDate(date);

        // Then
        assertEquals(2, tickets.size());
        assertTrue(tickets.stream().allMatch(t ->
                t.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(date)
        ));

    }

    @Test
    @Order(8)
    void givenNewFromPlanet_whenUpdateFromPlanet_thenTicketIsUpdated() {
        // Given
        Ticket ticket = ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        Planet newFromPlanet = planetDao.save(new Planet("VEN", "Venus"));
        ticket.setFromPlanet(newFromPlanet);

        // When
        ticketDao.updateFromPlanet(ticket.getId(), newFromPlanet);

        // Then
        Optional<Ticket> updated = ticketDao.findById(ticket.getId());
        assertTrue(updated.isPresent());
        assertEquals("VEN", updated.get().getFromPlanet().getId());
    }

    @Test
    @Order(9)
    void givenNewToPlanet_whenUpdateToPlanet_thenTicketIsUpdated() {
        // Given
        Ticket ticket = ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        Planet newToPlanet = planetDao.save(new Planet("JUP", "Jupiter"));
        ticket.setToPlanet(newToPlanet);

        // When
        ticketDao.updateToPlanet(ticket.getId(), newToPlanet);

        // Then
        Optional<Ticket> updated = ticketDao.findById(ticket.getId());
        assertTrue(updated.isPresent());
        assertEquals("JUP", updated.get().getToPlanet().getId());
    }

    @Test
    @Order(10)
    void givenTicket_whenDelete_thenTicketIsRemoved() {
        // Given
        Ticket ticket = ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        ticketDao.delete(ticket);

        // Then
        Optional<Ticket> found = ticketDao.findById(ticket.getId());
        assertFalse(found.isPresent());
    }

    @Test
    @Order(11)
    void givenClientId_whenDeleteAllByClientId_thenTicketsAreDeleted() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        ticketDao.deleteAllByClientId(savedClient.getId());

        // Then
        List<Ticket> tickets = ticketDao.findAllByClient(savedClient.getId());
        assertTrue(tickets.isEmpty());    }

    @Test
    @Order(12)
    void givenFromPlanetId_whenDeleteAllByFromPlanetId_thenTicketsAreDeleted() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        ticketDao.deleteAllByFromPlanetId(savedFromPlanet.getId());

        // Then
        List<Ticket> tickets = ticketDao.findAllByFromPlanet(savedFromPlanet.getId());
        assertTrue(tickets.isEmpty());    }

    @Test
    @Order(13)
    void givenToPlanetId_whenDeleteAllByToPlanetId_thenTicketsAreDeleted() {
        // Given
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));
        ticketDao.save(new Ticket(savedClient, savedFromPlanet, savedToPlanet, Instant.now()));

        // When
        ticketDao.deleteAllByToPlanetId(savedToPlanet.getId());

        // Then
        List<Ticket> tickets = ticketDao.findAllByToPlanet(savedToPlanet.getId());
        assertTrue(tickets.isEmpty());
    }

    @AfterEach
    void cleanDb() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.createMutationQuery("DELETE FROM Ticket").executeUpdate();
        session.createMutationQuery("DELETE FROM Client").executeUpdate();
        session.createMutationQuery("DELETE FROM Planet").executeUpdate();
        tx.commit();
        session.close();
    }
}
