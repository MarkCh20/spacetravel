package com.spacetravel.service;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.exception.ClientNotFoundException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientServiceTest {

    private static ClientCrudServiceImpl service;
    private static Long createdClientId;

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

        service = new ClientCrudServiceImpl(new ClientDaoImpl());
    }


    @Test
    @Order(1)
    void givenClientName_whenCreateClient_thenClientIsPersistedWithId() {
        // Given
        String clientName = "Test Client";

        // When
        Client actualClient = service.create(clientName);

        // Then
        assertNotNull(actualClient.getId(), "Created client ID should not be null");
        assertEquals(clientName, actualClient.getName(), "Client name should match the input");
        createdClientId = actualClient.getId();
    }

    @Test
    @Order(2)
    void givenExistingClientId_whenFindById_thenReturnClient() {
        // When
        Client actualClient = service.findById(createdClientId);

        // Then
        assertNotNull(actualClient, "Client should be found by existing ID");
        String expectedName = "Test Client";
        String actualName = actualClient.getName();
        assertEquals(expectedName, actualName, "Client name should match expected");
    }

    @Test
    @Order(3)
    void givenNonExistingClientId_whenFindById_thenReturnEmptyOptional() {
        // Given
        Long invalidId = 9999L;

        // Then
        assertThrows(ClientNotFoundException.class,
                () -> service.findById(invalidId),
                "Finding non-existing client should throw ClientNotFoundException");
    }

    @Test
    @Order(4)
    void givenClientsExist_whenFindAll_thenReturnNonEmptyList() {
        // When
        List<Client> actualClients = service.findAll();

        // Then
        assertFalse(actualClients.isEmpty(), "Client list should not be empty");
    }

    @Test
    @Order(5)
    void givenExistingClientIdAndNewName_whenUpdateClient_thenClientNameIsUpdated() {
        // Given
        String updatedName = "Updated Name";

        // When
        Client actualUpdatedClient = service.update(createdClientId, updatedName);

        // Then
        assertEquals(updatedName, actualUpdatedClient.getName(), "Client name should be updated");
    }

    @Test
    @Order(6)
    void givenNonExistingClientId_whenUpdateClient_thenThrowClientNotFoundException() {
        // Given
        Long invalidId = 9999L;
        String newName = "New Name";

        // When & Then
        assertThrows(ClientNotFoundException.class,
                () -> service.update(invalidId, newName),
                "Updating non-existing client should throw ClientNotFoundException");
    }

    @Test
    @Order(7)
    void givenExistingClientId_whenDeleteClient_thenClientIsRemoved() {
        // When
        assertDoesNotThrow(() -> service.delete(createdClientId), "Deleting existing client should not throw");

        // Then
        assertThrows(ClientNotFoundException.class,
                () -> service.findById(createdClientId),
                "After deletion, client should not be found and throw ClientNotFoundException");
    }

    @Test
    @Order(8)
    void givenNonExistingClientId_whenDeleteClient_thenThrowClientNotFoundException() {
        // Given
        Long invalidId = 9999L;

        // When & Then
        assertThrows(ClientNotFoundException.class,
                () -> service.delete(invalidId),
                "Deleting non-existing client should throw ClientNotFoundException");
    }
}
