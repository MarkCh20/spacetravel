package com.spacetravel.integration;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.dao.PlanetDaoImpl;
import com.spacetravel.dao.TicketDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.entity.Planet;
import com.spacetravel.entity.Ticket;
import com.spacetravel.exception.TicketNotFoundException;
import com.spacetravel.service.ClientCrudServiceImpl;
import com.spacetravel.service.PlanetCrudServiceImpl;
import com.spacetravel.service.TicketCrudServiceImpl;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TicketIntegrationTest {

    private TicketCrudServiceImpl ticketService;
    private ClientCrudServiceImpl clientService;
    private PlanetCrudServiceImpl planetService;

    @BeforeAll
    void setUpDatabase() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();

        flyway.migrate();
    }

    @BeforeEach
    void setUpServices() {
        TicketDaoImpl ticketDao = new TicketDaoImpl();
        ClientDaoImpl clientDao = new ClientDaoImpl();
        PlanetDaoImpl planetDao = new PlanetDaoImpl();

        this.ticketService = new TicketCrudServiceImpl(ticketDao, planetDao);
        this.clientService = new ClientCrudServiceImpl(clientDao, ticketDao);
        this.planetService = new PlanetCrudServiceImpl(planetDao, ticketDao);
    }

    private Client createClient(String name) {
        return clientService.create(name);
    }

    private Planet createPlanet(String id, String name) {
        return planetService.create(id, name);
    }

    private Ticket createTicket(Client client, Planet from, Planet to, Instant createdAt) {
        return ticketService.create(new Ticket(client, from, to, createdAt));
    }

    @Test
    @Order(1)
    void givenValidTicket_whenCreate_thenTicketIsSaved() {
        Client client = createClient("CreateClient");
        Planet from = createPlanet("EARTH1", "Earth1");
        Planet to = createPlanet("MARS1", "Mars1");
        Instant now = Instant.now();

        Ticket ticket = createTicket(client, from, to, now);

        assertNotNull(ticket.getId());
        assertEquals(client.getId(), ticket.getClient().getId());
        assertEquals(from.getId(), ticket.getFromPlanet().getId());
        assertEquals(to.getId(), ticket.getToPlanet().getId());
        assertEquals(now, ticket.getCreatedAt());
    }

    @Test
    @Order(2)
    void givenSavedTicket_whenFindById_thenReturnTicket() {
        Client client = createClient("FindByIdClient");
        Planet from = createPlanet("FIND_FROM", "FindFrom");
        Planet to = createPlanet("FIND_TO", "FindTo");
        Instant now = Instant.now();

        Ticket created = createTicket(client, from, to, now);
        Ticket found = ticketService.findById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    @Order(3)
    void givenMultipleTickets_whenFindAll_thenReturnAllTickets() {
        Client client = createClient("FindAllClient");
        Planet from = createPlanet("EARTH2", "Earth2");
        Planet to = createPlanet("MARS2", "Mars2");

        createTicket(client, from, to, Instant.now());
        createTicket(client, to, from, Instant.now());

        List<Ticket> allTickets = ticketService.findAll();

        assertTrue(allTickets.size() >= 2);
    }

    @Test
    @Order(4)
    void givenTicketId_whenDelete_thenTicketIsDeleted() {
        Client client = createClient("DeleteClient");
        Planet from = createPlanet("DEL_FROM", "DelFrom");
        Planet to = createPlanet("DEL_TO", "DelTo");
        Ticket ticket = createTicket(client, from, to, Instant.now());
        Long ticketId = ticket.getId();

        ticketService.delete(ticketId);

        assertThrows(TicketNotFoundException.class, () -> ticketService.findById(ticketId));
    }

    @Test
    @Order(5)
    void givenClientId_whenFindByClient_thenReturnTickets() {
        Client client1 = createClient("Client1");
        Client client2 = createClient("Client2");
        Planet from = createPlanet("EARTH3", "Earth3");
        Planet to = createPlanet("MARS3", "Mars3");

        createTicket(client1, from, to, Instant.now());
        createTicket(client2, from, to, Instant.now());

        List<Ticket> ticketsClient1 = ticketService.findAllByClient(client1.getId());
        List<Ticket> ticketsClient2 = ticketService.findAllByClient(client2.getId());

        assertEquals(1, ticketsClient1.size());
        assertEquals(1, ticketsClient2.size());
        assertTrue(ticketsClient1.stream().allMatch(t -> t.getClient().getId().equals(client1.getId())));
        assertTrue(ticketsClient2.stream().allMatch(t -> t.getClient().getId().equals(client2.getId())));
    }

    @Test
    @Order(6)
    void givenFromPlanetId_whenFindByFromPlanet_thenReturnTickets() {
        Client client = createClient("FromPlanetClient");
        Planet from1 = createPlanet("FROM1", "FromPlanet1");
        Planet from2 = createPlanet("FROM2", "FromPlanet2");
        Planet to = createPlanet("FROM_TO", "FromTo");

        createTicket(client, from1, to, Instant.now());
        createTicket(client, from2, to, Instant.now());

        List<Ticket> ticketsFrom1 = ticketService.findAllByFromPlanet(from1.getId());
        List<Ticket> ticketsFrom2 = ticketService.findAllByFromPlanet(from2.getId());

        assertEquals(1, ticketsFrom1.size());
        assertEquals(1, ticketsFrom2.size());
        assertTrue(ticketsFrom1.stream().allMatch(t -> t.getFromPlanet().getId().equals(from1.getId())));
        assertTrue(ticketsFrom2.stream().allMatch(t -> t.getFromPlanet().getId().equals(from2.getId())));
    }

    @Test
    @Order(7)
    void givenToPlanetId_whenFindByToPlanet_thenReturnTickets() {
        Client client = createClient("ToPlanetClient");
        Planet to1 = createPlanet("TO1", "ToPlanet1");
        Planet to2 = createPlanet("TO2", "ToPlanet2");
        Planet from = createPlanet("TO_FROM", "ToFrom");

        createTicket(client, from, to1, Instant.now());
        createTicket(client, from, to2, Instant.now());

        List<Ticket> ticketsTo1 = ticketService.findAllByToPlanet(to1.getId());
        List<Ticket> ticketsTo2 = ticketService.findAllByToPlanet(to2.getId());

        assertEquals(1, ticketsTo1.size());
        assertEquals(1, ticketsTo2.size());
        assertTrue(ticketsTo1.stream().allMatch(t -> t.getToPlanet().getId().equals(to1.getId())));
        assertTrue(ticketsTo2.stream().allMatch(t -> t.getToPlanet().getId().equals(to2.getId())));
    }

    @Test
    @Order(8)
    void givenTicketWithDifferentCreatedAt_whenFindByDate_thenReturnCorrectTickets() {
        Client client = createClient("DateClient");
        Planet from = createPlanet("EARTH4", "Earth4");
        Planet to = createPlanet("MARS4", "Mars4");

        Instant nowInstant  = Instant.now();
        LocalDate now = nowInstant.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant beforeInstant = nowInstant.minusSeconds(3600);

        Ticket ticketNow = createTicket(client, from, to, nowInstant);
        createTicket(client, from, to, beforeInstant);

        List<Ticket> ticketsForNow = ticketService.findAllByDate(now);

        assertTrue(ticketsForNow.stream().anyMatch(t -> t.getId().equals(ticketNow.getId())));
        assertTrue(ticketsForNow.stream().allMatch(t -> t.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate().equals(now)));
    }

    @Test
    @Order(9)
    void givenValidTicketIdAndNewFromPlanet_whenUpdateFromPlanet_thenFromPlanetIsUpdated() {
        Client client = createClient("UpdateFromClient");
        Planet oldFrom = createPlanet("EARTH5", "Earth5");
        Planet newFrom = createPlanet("PLUTO5", "Pluto5");
        Planet to = createPlanet("MARS5", "Mars5");

        Ticket ticket = createTicket(client, oldFrom, to, Instant.now());

        ticketService.updateFromPlanet(ticket.getId(), newFrom.getId());

        Ticket updated = ticketService.findById(ticket.getId());

        assertEquals(newFrom.getId(), updated.getFromPlanet().getId());
    }

    @Test
    @Order(10)
    void givenValidTicketIdAndNewToPlanet_whenUpdateToPlanet_thenToPlanetIsUpdated() {
        Client client = createClient("UpdateToClient");
        Planet from = createPlanet("EARTH6", "Earth6");
        Planet oldTo = createPlanet("MARS6", "Mars6");
        Planet newTo = createPlanet("PLUTO6", "Pluto6");

        Ticket ticket = createTicket(client, from, oldTo, Instant.now());

        ticketService.updateToPlanet(ticket.getId(), newTo.getId());

        Ticket updated = ticketService.findById(ticket.getId());

        assertEquals(newTo.getId(), updated.getToPlanet().getId());
    }

    @Test
    @Order(11)
    void givenClientIdWithTickets_whenDeleteAllByClientId_thenTicketsDeleted() {
        Client client1 = createClient("DelAllClient1");
        Client client2 = createClient("DelAllClient2");
        Planet from = createPlanet("EARTH7", "Earth7");
        Planet to = createPlanet("MARS7", "Mars7");

        createTicket(client1, from, to, Instant.now());
        createTicket(client1, from, to, Instant.now());
        createTicket(client2, from, to, Instant.now());

        ticketService.deleteAllByClientId(client1.getId());

        List<Ticket> client1Tickets = ticketService.findAllByClient(client1.getId());
        List<Ticket> client2Tickets = ticketService.findAllByClient(client2.getId());

        assertTrue(client1Tickets.isEmpty());
        assertFalse(client2Tickets.isEmpty());
    }

    @Test
    @Order(12)
    void givenFromPlanetIdWithTickets_whenDeleteAllByFromPlanetId_thenTicketsDeleted() {
        Client client = createClient("DelAllFromClient");
        Planet from1 = createPlanet("EARTH8", "Earth8");
        Planet from2 = createPlanet("MARS8", "Mars8");
        Planet to = createPlanet("PLUTO8", "Pluto8");

        createTicket(client, from1, to, Instant.now());
        createTicket(client, from1, to, Instant.now());
        createTicket(client, from2, to, Instant.now());

        ticketService.deleteAllByFromPlanetId(from1.getId());

        List<Ticket> from1Tickets = ticketService.findAllByFromPlanet(from1.getId());
        List<Ticket> from2Tickets = ticketService.findAllByFromPlanet(from2.getId());

        assertTrue(from1Tickets.isEmpty());
        assertFalse(from2Tickets.isEmpty());
    }

    @Test
    @Order(13)
    void givenToPlanetIdWithTickets_whenDeleteAllByToPlanetId_thenTicketsDeleted() {
        Client client = createClient("DelAllToClient");
        Planet to1 = createPlanet("EARTH10", "Earth10");
        Planet to2 = createPlanet("MARS10", "Mars10");
        Planet from = createPlanet("PLUTO10", "Pluto10");

        createTicket(client, from, to1, Instant.now());
        createTicket(client, from, to1, Instant.now());
        createTicket(client, from, to2, Instant.now());

        ticketService.deleteAllByToPlanetId(to1.getId());

        List<Ticket> to1Tickets = ticketService.findAllByToPlanet(to1.getId());
        List<Ticket> to2Tickets = ticketService.findAllByToPlanet(to2.getId());

        assertTrue(to1Tickets.isEmpty());
        assertFalse(to2Tickets.isEmpty());
    }

    @Test
    @Order(14)
    void givenNonExistingTicket_whenFindById_thenReturnNull() {
        TicketNotFoundException exception = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.findById(999999L)
        );

        assertEquals("Ticket with id 999999 not found.", exception.getMessage());

    }

    @Test
    @Order(15)
    void givenNonExistingClientId_whenFindByClient_thenReturnEmptyList() {
        List<Ticket> tickets = ticketService.findAllByClient(999999L);
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(16)
    void givenNonExistingFromPlanetId_whenFindByFromPlanet_thenReturnEmptyList() {
        List<Ticket> tickets = ticketService.findAllByFromPlanet("NON_EXISTING_FROM");
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(17)
    void givenNonExistingToPlanetId_whenFindByToPlanet_thenReturnEmptyList() {
        List<Ticket> tickets = ticketService.findAllByToPlanet("NON_EXISTING_TO");
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(18)
    void givenNonExistingCreatedAt_whenFindByDate_thenReturnEmptyList() {
        // Pick a date very far in the past unlikely to exist
        List<Ticket> tickets = ticketService.findAllByDate(LocalDate.of(1900, 1, 1));
        assertTrue(tickets.isEmpty());
    }

    @Test
    @Order(19)
    void givenInvalidTicketId_whenUpdateFromPlanet_thenThrowException() {
        Planet newFrom = createPlanet("INVALID", "InvalidPlanet");
        Long invalidTicketId = 999999L;

        assertThrows(TicketNotFoundException.class, () ->
                ticketService.updateFromPlanet(invalidTicketId, newFrom.getId())
        );

    }

    @Test
    @Order(20)
    void givenInvalidTicketId_whenUpdateToPlanet_thenThrowException() {
        Planet newTo = createPlanet("INVALID_TO", "InvalidTo");
        Long invalidTicketId = 999999L;

        assertThrows(TicketNotFoundException.class, () ->
                ticketService.updateToPlanet(invalidTicketId, newTo.getId())
        );
    }

    @Test
    @Order(21)
    void givenNonExistingId_whenDelete_thenThrowTicketNotFoundException() {
        Long invalidTicketId = 999999L;

        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.delete(invalidTicketId)
        );
        assertEquals("Attempted to delete nonexistent ticket with id: 999999", ex.getMessage());
    }

    @Test
    @Order(22)
    void givenNonExistingClientId_whenDeleteAllByClientId_thenThrowTicketNotFoundException() {
        Long invalidClientId = 999999L;

        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.deleteAllByClientId(invalidClientId)
        );
        assertEquals("No tickets found for clientId: 999999", ex.getMessage());
    }

    @Test
    @Order(23)
    void givenNonExistingFromPlanetId_whenDeleteAllByFromPlanetId_thenThrowTicketNotFoundException() {
        String invalidFromPlanetId = "NON_EXIST";

        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.deleteAllByFromPlanetId(invalidFromPlanetId)
        );
        assertEquals("No tickets found for fromPlanetId: NON_EXIST", ex.getMessage());
    }

    @Test
    @Order(24)
    void givenNonExistingToPlanetId_whenDeleteAllByToPlanetId_thenThrowTicketNotFoundException() {
        String invalidToPlanetId = "INVALID";

        TicketNotFoundException ex = assertThrows(
                TicketNotFoundException.class,
                () -> ticketService.deleteAllByToPlanetId(invalidToPlanetId)
        );
        assertEquals("No tickets found for toPlanetId: INVALID", ex.getMessage());
    }
}
