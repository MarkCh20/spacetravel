package com.spacetravel.integration;

import com.spacetravel.dao.ClientDaoImpl;
import com.spacetravel.entity.Client;
import com.spacetravel.exception.ClientNotFoundException;
import com.spacetravel.service.ClientCrudServiceImpl;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientIntegrationTest {

    private static ClientCrudServiceImpl clientService;
    private static Long testClientId;

    @BeforeAll
    static void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();

        flyway.migrate();
        clientService = new ClientCrudServiceImpl(new ClientDaoImpl());
    }

    @Test
    @Order(1)
    void givenClientName_whenCreateClient_thenClientIsPersisted() {
        // Given
        String expectedName = "Integration User";

        // When
        Client actualClient = clientService.create(expectedName);

        // Then
        assertNotNull(actualClient.getId());
        assertEquals(expectedName, actualClient.getName());

        testClientId = actualClient.getId();
    }

    @Test
    @Order(2)
    void givenClientId_whenFindById_thenReturnClient() {
        // When
        Client actualClient = clientService.findById(testClientId);

        // Then
        assertNotNull(actualClient);
        assertEquals("Integration User", actualClient.getName());    }


    @Test
    @Order(3)
    void whenFindAllClients_thenReturnNonEmptyList() {
        // When
        List<Client> actualClients = clientService.findAll();

        // Then
        assertFalse(actualClients.isEmpty());
    }

    @Test
    @Order(4)
    void givenClientId_whenUpdateClient_thenClientIsUpdated() {
        // Given
        String newName = "Updated Integration";

        // When
        Client actualUpdatedClient = clientService.update(testClientId, newName);

        // Then
        assertEquals(newName, actualUpdatedClient.getName());
    }

    @Test
    @Order(5)
    void givenClientId_whenDeleteClient_thenClientIsDeleted() {
        // When / Then
        assertDoesNotThrow(() -> clientService.delete(testClientId));

        // Then
        assertThrows(ClientNotFoundException.class, () -> clientService.findById(testClientId));
    }

    @Test
    @Order(6)
    void givenNonExistingClientId_whenDeleteClient_thenThrowClientNotFoundException() {
        // Given
        Long invalidId = 9999L;

        // When / Then
        assertThrows(ClientNotFoundException.class, () -> clientService.delete(invalidId));
    }

    @Test
    @Order(7)
    void givenNonExistingClientId_whenUpdateClient_thenThrowClientNotFoundException() {
        Long invalidId = 9999L;

        // When / Then
        assertThrows(ClientNotFoundException.class, () -> clientService.update(invalidId, "New Name"));    }

    @Test
    @Order(8)
    void givenNonExistingClientId_whenFindById_thenReturnEmptyOptional() {
        // Given
        Long invalidId = 9999L;

        // Then
        assertThrows(ClientNotFoundException.class, () -> clientService.findById(invalidId));
    }
}
