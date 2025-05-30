package com.spacetravel.service;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.dao.PlanetDaoImpl;
import com.spacetravel.dao.TicketDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import com.spacetravel.exception.TicketNotFoundException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketServiceTest {

    private static TicketCrudServiceImpl service;
    private ClientCrudServiceImpl clientService;
    private PlanetCrudServiceImpl planetService;

    @BeforeAll
    static void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource(
                        "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
                        "sa",
                        ""
                )
                .locations("filesystem:src/test/resources/db/migration")
                .load();

        flyway.migrate();
    }

    @BeforeEach
    void setUpService() {
        // Use real DAO implementations connecting to the test DB
        ClientDaoImpl clientDao = new ClientDaoImpl();
        TicketDaoImpl ticketDao = new TicketDaoImpl();
        PlanetDaoImpl planetDao = new PlanetDaoImpl();

        clientService = new ClientCrudServiceImpl(clientDao, ticketDao);
        planetService = new PlanetCrudServiceImpl(planetDao, ticketDao);
        service = new TicketCrudServiceImpl(ticketDao, planetDao);
    }

    private Client createAndSaveClient(String name) {
        return clientService.create(name);
    }

    private Planet createAndSavePlanet(String id, String name) {
        return planetService.create(id, name);
    }

    private Ticket createAndSaveTicket(Client client, Planet from, Planet to, Instant createdAt) {
        return service.create(new Ticket(client, from, to, createdAt));
    }

    @Test
    @Order(1)
    void givenValidTicket_whenCreate_thenTicketIsSaved() {
        // Given
        Client client = createAndSaveClient("Alice");
        Planet from = createAndSavePlanet("EARTH-1", "Earth-1");
        Planet to = createAndSavePlanet("MARS-1", "Mars-1");
        Ticket ticket = new Ticket(client, from, to, Instant.now());

        // When
        Ticket created = service.create(ticket);

        // Then
        assertNotNull(created);
        assertTrue(created.getId() > 0);
        assertEquals(client.getName(), created.getClient().getName());
    }

    @Test
    @Order(2)
    void givenSavedTicket_whenFindById_thenReturnTicket() {
        // Given
        Client client = createAndSaveClient("Bob");
        Planet from = createAndSavePlanet("EARTH-2", "Earth-2");
        Planet to = createAndSavePlanet("VENUS-2", "Venus-2");
        Ticket ticket = createAndSaveTicket(client, from, to, Instant.now());

        // When
        Ticket found = service.findById(ticket.getId());

        // Then
        assertNotNull(found);
        assertEquals(ticket.getId(), found.getId());
    }

    @Test
    @Order(3)
    void givenNonExistingTicket_whenFindById_thenReturnNull() {
        // When / Then
        TicketNotFoundException exception = assertThrows(
                TicketNotFoundException.class,
                () -> service.findById(9999L)
        );

        assertEquals("Ticket with id 9999 not found.", exception.getMessage());
    }

    @Test
    @Order(4)
    void givenMultipleTickets_whenFindAll_thenReturnAllTickets() {
        // Given
        Client client1 = createAndSaveClient("Ben1");
        Client client2 = createAndSaveClient("Ben2");
        Planet earth = createAndSavePlanet("EARTH-3", "Earth-3");
        Planet mars = createAndSavePlanet("MARS-3", "Mars-3");
        createAndSaveTicket(client1, earth, mars, Instant.now());
        createAndSaveTicket(client2, earth, mars, Instant.now());

        // When
        List<Ticket> allTickets = service.findAll();

        // Then
        assertTrue(allTickets.size() >= 2);
    }

    @Test
    @Order(5)
    void givenClientId_whenFindByClient_thenReturnTickets() {
        // Given
        Client client1 = createAndSaveClient("Ben1");
        Client client2 = createAndSaveClient("Ben2");
        Planet earth = createAndSavePlanet("EARTH-4", "Earth-4");
        Planet mars = createAndSavePlanet("MARS-4", "Mars-4");

        Ticket ticket1 = createAndSaveTicket(client1, earth, mars, Instant.now());
        createAndSaveTicket(client2, earth, mars, Instant.now());

        // When
        List<Ticket> tickets = service.findAllByClient(client1.getId());

        // Then
        assertEquals(1, tickets.size());
        assertEquals(ticket1.getId(), tickets.get(0).getId());
    }

    @Test
    @Order(6)
    void givenNonExistingClientId_whenFindByClient_thenReturnEmptyList() {
        // Given no tickets for this client ID
        long fakeClientId = 99999L;

        // When
        List<Ticket> tickets = service.findAllByClient(fakeClientId);

        // Then
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(7)
    void givenFromPlanetId_whenFindByFromPlanet_thenReturnTickets() {
        // Given
        Client client = createAndSaveClient("Nick");
        Planet earth = createAndSavePlanet("EARTH-5", "Earth-5");
        Planet mars = createAndSavePlanet("MARS-5", "Mars-5");
        Planet venus = createAndSavePlanet("VENUS-5", "Venus-5");

        Ticket ticket1 = createAndSaveTicket(client, earth, mars, Instant.now());
        createAndSaveTicket(client, venus, mars, Instant.now());

        // When
        List<Ticket> tickets = service.findAllByFromPlanet("EARTH-5");

        // Then
        assertEquals(1, tickets.size());
        assertEquals(ticket1.getId(), tickets.get(0).getId());
    }

    @Test
    @Order(8)
    void givenNonExistingFromPlanetId_whenFindByFromPlanet_thenReturnEmptyList() {
        // Given no tickets from this planet ID
        String fakePlanetId = "NON_EXISTING_PLANET";

        // When
        List<Ticket> tickets = service.findAllByFromPlanet(fakePlanetId);

        // Then
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(9)
    void givenToPlanetId_whenFindByToPlanet_thenReturnTickets() {
        // Given
        Client client = createAndSaveClient("Tony");
        Planet earth = createAndSavePlanet("EARTH-6", "Earth-6");
        Planet mars = createAndSavePlanet("MARS-6", "Mars-6");
        Planet venus = createAndSavePlanet("VENUS-6", "Venus-6");

        Ticket ticket1 = createAndSaveTicket(client, earth, mars, Instant.now());
        createAndSaveTicket(client, earth, venus, Instant.now());

        // When
        List<Ticket> tickets = service.findAllByToPlanet("MARS-6");

        // Then
        assertEquals(1, tickets.size());
        assertEquals(ticket1.getId(), tickets.get(0).getId());
    }

    @Test
    @Order(10)
    void givenNonExistingToPlanetId_whenFindByToPlanet_thenReturnEmptyList() {
        // Given no tickets to this planet ID
        String fakePlanetId = "NON_EXISTING_PLANET";

        // When
        List<Ticket> tickets = service.findAllByToPlanet(fakePlanetId);

        // Then
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(11)
    void givenTicketWithDifferentCreatedAt_whenFindByDate_thenReturnCorrectTickets() {
        // Given
        Client client = createAndSaveClient("Bob");
        Planet earth = createAndSavePlanet("EARTH-7", "Earth-7");
        Planet mars = createAndSavePlanet("MARS-7", "Mars-7");

        Instant fixedInstant = Instant.parse("2024-01-01T00:00:00Z").truncatedTo(ChronoUnit.MILLIS);

        Ticket ticket = createAndSaveTicket(client, earth, mars, fixedInstant);

        LocalDate localDate = fixedInstant.atZone(ZoneOffset.UTC).toLocalDate();

        // When
        List<Ticket> tickets = service.findAllByDate(localDate);

        // Then
        assertEquals(1, tickets.size());
        assertEquals(fixedInstant, tickets.get(0).getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @Order(12)
    void givenNonExistingCreatedAt_whenFindByDate_thenReturnEmptyList() {
        // Given no tickets created at this timestamp
        Instant distantPast = Instant.parse("2000-01-01T00:00:00Z");

        LocalDate localDate = distantPast.atZone(ZoneOffset.UTC).toLocalDate();


        // When
        List<Ticket> tickets = service.findAllByDate(localDate);

        // Then
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(13)
    void givenValidTicketIdAndNewFromPlanet_whenUpdateFromPlanet_thenFromPlanetIsUpdated() {
        // Given
        Client client = createAndSaveClient("TestClient");
        Planet earth = createAndSavePlanet("EARTH-8", "Earth-8");
        Planet mars = createAndSavePlanet("MARS-8", "Mars-8");
        Planet jupiter = createAndSavePlanet("JUP-8", "Jupiter-8");

        Ticket ticket = createAndSaveTicket(client, earth, mars, Instant.now());

        // When
        service.updateFromPlanet(ticket.getId(), jupiter.getId());
        Ticket updated = service.findById(ticket.getId());

        // Then
        assertEquals("JUP-8", updated.getFromPlanet().getId());
    }

    @Test
    @Order(14)
    void givenInvalidTicketId_whenUpdateFromPlanet_thenThrowException() {
        // Given
        Planet mars = createAndSavePlanet("MARS-9", "Mars-9");

        // When & Then
        assertThrows(TicketNotFoundException.class, () ->
                service.updateFromPlanet(99999L, mars.getId())
        );
    }

    @Test
    @Order(15)
    void givenValidTicketIdAndNewToPlanet_whenUpdateToPlanet_thenToPlanetIsUpdated() {
        // Given
        Client client = createAndSaveClient("TestClient");
        Planet earth = createAndSavePlanet("EARTH-10", "Earth-10");
        Planet mars = createAndSavePlanet("MARS-10", "Mars-10");
        Planet venus = createAndSavePlanet("VEN-10", "Venus-10");

        Ticket ticket = createAndSaveTicket(client, earth, mars, Instant.now());

        // When
        service.updateToPlanet(ticket.getId(), venus.getId());
        Ticket updated = service.findById(ticket.getId());

        // Then
        assertEquals("VEN-10", updated.getToPlanet().getId());
    }

    @Test
    @Order(16)
    void givenInvalidTicketId_whenUpdateToPlanet_thenThrowException() {
        // Given
        Planet venus = createAndSavePlanet("VEN-11", "Venus-11");

        // When & Then
        assertThrows(TicketNotFoundException.class, () ->
                service.updateToPlanet(99999L, venus.getId())
        );
    }

    @Test
    @Order(17)
    void givenTicketId_whenDelete_thenTicketIsDeleted() {
        // Given
        Client client = createAndSaveClient("Ben");
        Planet earth = createAndSavePlanet("EARTH-12", "Earth-12");
        Planet mars = createAndSavePlanet("MARS-12", "Mars-12");
        Ticket ticket = createAndSaveTicket(client, earth, mars, Instant.now());
        long id = ticket.getId();

        // When
        service.delete(id);

        // Then
        assertThrows(TicketNotFoundException.class, () -> service.findById(id));
    }

    @Test
    @Order(18)
    void givenNonExistingId_whenDelete_thenThrowTicketNotFoundException() {
        // When & Then
        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> service.delete(12345L)
        );
        assertEquals("Attempted to delete nonexistent ticket with id: 12345", ex.getMessage());
    }

    @Test
    @Order(19)
    void givenClientIdWithTickets_whenDeleteAllByClientId_thenTicketsDeleted() {
        // Given
        Client client = createAndSaveClient("BulkClient");
        Planet from = createAndSavePlanet("EARTH-13", "Earth-13");
        Planet to = createAndSavePlanet("MARS-13", "Mars-13");

        createAndSaveTicket(client, from, to, Instant.now());
        createAndSaveTicket(client, from, to, Instant.now());

        // When
        service.deleteAllByClientId(client.getId());

        // Then
        List<Ticket> remaining = service.findAllByClient(client.getId());
        assertTrue(remaining.isEmpty());
    }

    @Test
    @Order(20)
    void givenNonExistingClientId_whenDeleteAllByClientId_thenThrowTicketNotFoundException() {
        // When & Then
        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> service.deleteAllByClientId(99999L)
        );
        assertEquals("No tickets found for clientId: 99999", ex.getMessage());
    }

    @Test
    @Order(21)
    void givenFromPlanetIdWithTickets_whenDeleteAllByFromPlanetId_thenTicketsDeleted() {
        // Given
        Client client = createAndSaveClient("PlanetClient");
        Planet earth = createAndSavePlanet("EARTH-14", "Earth-14");
        Planet mars = createAndSavePlanet("MARS-14", "Mars-14");

        createAndSaveTicket(client, earth, mars, Instant.now());
        createAndSaveTicket(client, earth, mars, Instant.now());

        // When
        service.deleteAllByFromPlanetId("EARTH-14");

        // Then
        List<Ticket> remaining = service.findAllByFromPlanet("EARTH-14");
        assertTrue(remaining.isEmpty());
    }

    @Test
    @Order(22)
    void givenNonExistingFromPlanetId_whenDeleteAllByFromPlanetId_thenThrowTicketNotFoundException() {
        // When & Then
        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> service.deleteAllByFromPlanetId("NON_EXIST")
        );
        assertEquals("No tickets found for fromPlanetId: NON_EXIST", ex.getMessage());
    }

    @Test
    @Order(23)
    void givenToPlanetIdWithTickets_whenDeleteAllByToPlanetId_thenTicketsDeleted() {
        // Given
        Client client = createAndSaveClient("ToPlanetClient");
        Planet earth = createAndSavePlanet("EARTH-15", "Earth-15");
        Planet mars = createAndSavePlanet("MARS-15", "Mars-15");

        createAndSaveTicket(client, earth, mars, Instant.now());
        createAndSaveTicket(client, earth, mars, Instant.now());

        // When
        service.deleteAllByToPlanetId("MARS-15");

        // Then
        List<Ticket> remaining = service.findAllByToPlanet("MARS-15");
        assertTrue(remaining.isEmpty());
    }

    @Test
    @Order(24)
    void givenNonExistingToPlanetId_whenDeleteAllByToPlanetId_thenThrowTicketNotFoundException() {
        // When & Then
        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> service.deleteAllByToPlanetId("NON_EXIST")
        );
        assertEquals("No tickets found for toPlanetId: NON_EXIST", ex.getMessage());

    }
}
