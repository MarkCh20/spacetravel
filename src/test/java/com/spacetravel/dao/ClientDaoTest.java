package com.spacetravel.dao;

import com.spacetravel.entity.Client;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientDaoTest {

    private ClientDaoImpl clientDao;

    @BeforeAll
    void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();
        flyway.migrate();

        clientDao = new ClientDaoImpl();
    }


    @Test
    void givenNewClient_whenSave_thenClientIsPersisted() {
        // Given
        Client newClient = new Client("John Doe");

        // When
        clientDao.save(newClient);

        // Then
        Client actualClient = clientDao.findById(newClient.getId()).orElse(null);
        assertNotNull(actualClient);
        String expectedName = "John Doe";
        String actualName = actualClient.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void givenExistingClient_whenUpdate_thenClientNameIsUpdated() {
        // Given
        Client client = new Client("Old Name");
        clientDao.save(client);
        String expectedName = "New Name";

        // When
        client.setName(expectedName);
        clientDao.update(client);

        // Then
        Client actualClient = clientDao.findById(client.getId()).orElse(null);
        assertNotNull(actualClient);
        String actualName = actualClient.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void givenExistingClient_whenDelete_thenClientIsRemoved() {
        // Given
        Client clientToDelete = new Client("To Delete");
        clientDao.save(clientToDelete);

        // When
        clientDao.delete(clientToDelete);

        // Then
        Client actualClient = clientDao.findById(clientToDelete.getId()).orElse(null);
        assertNull(actualClient);
    }

    @Test
    void givenMultipleClients_whenFindAll_thenReturnListOfClients() {
        // Given
        Client client1 = new Client("Alice");
        Client client2 = new Client("Bob");
        clientDao.save(client1);
        clientDao.save(client2);

        // When
        List<Client> actualClients = clientDao.findAll();

        // Then
        assertTrue(actualClients.size() >= 2);
    }

    @Test
    void givenSavedClient_whenFindById_thenCorrectClientIsReturned() {
        // Given
        Client savedClient = new Client("Find Me");
        clientDao.save(savedClient);
        Long clientId = savedClient.getId();

        // When
        Client actualClient = clientDao.findById(clientId).orElse(null);

        // Then
        assertNotNull(actualClient, "Client should be found by ID");
        assertEquals("Find Me", actualClient.getName(), "Client name should match the saved one");
        assertEquals(clientId, actualClient.getId(), "Client ID should match the saved one");
    }
}