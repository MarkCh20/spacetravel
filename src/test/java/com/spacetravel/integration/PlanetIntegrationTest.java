package com.spacetravel.integration;

import com.spacetravel.entity.Planet;
import com.spacetravel.exception.PlanetNotFoundException;
import com.spacetravel.service.PlanetCrudService;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PlanetIntegrationTest {

    private static PlanetCrudService planetService;
    private static String testPlanetId;

    @BeforeAll
    static void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();

        flyway.migrate();
        planetService  = new PlanetCrudService();
    }

    @Test
    @Order(1)
    void givenPlanetIdAndName_whenCreatePlanet_thenPlanetIsPersisted() {
        // Given
        String expectedId = "X999";
        String expectedName = "Test Planet";

        // When
        Planet actualPlanet = planetService.create(expectedId, expectedName);

        // Then
        assertNotNull(actualPlanet.getId());
        assertEquals(expectedId, actualPlanet.getId());
        assertEquals(expectedName, actualPlanet.getName());

        testPlanetId = expectedId;
    }

    @Test
    @Order(2)
    void givenPlanetId_whenFindById_thenReturnPlanet() {
        // When
        Optional<Planet> actualPlanetOpt = planetService.findById(testPlanetId);

        // Then
        assertTrue(actualPlanetOpt.isPresent());
        assertEquals("Test Planet", actualPlanetOpt.get().getName());
    }

    @Test
    @Order(3)
    void whenFindAllPlanets_thenReturnNonEmptyList() {
        // When
        List<Planet> actualPlanets = planetService.findAll();

        // Then
        assertFalse(actualPlanets.isEmpty());
    }

    @Test
    @Order(4)
    void givenPlanetId_whenUpdatePlanet_thenPlanetIsUpdated() {
        // Given
        String newName = "Updated Planet";

        // When
        Planet actualUpdatedPlanet = planetService.update(testPlanetId, newName);

        // Then
        assertEquals(newName, actualUpdatedPlanet.getName());
    }

    @Test
    @Order(5)
    void givenPlanetId_whenDeletePlanet_thenPlanetIsDeleted() {
        // When / Then
        assertDoesNotThrow(() -> planetService.delete(testPlanetId));

        // When
        Optional<Planet> actualPlanetOpt = planetService.findById(testPlanetId);

        // Then
        assertTrue(actualPlanetOpt.isEmpty());
    }

    @Test
    @Order(6)
    void givenNonExistingPlanetId_whenDeletePlanet_thenThrowPlanetNotFoundException() {
        // Given
        String invalidId = "INVALID";

        // When / Then
        assertThrows(PlanetNotFoundException.class, () -> planetService.delete(invalidId));
    }

    @Test
    @Order(7)
    void givenNonExistingPlanetId_whenUpdatePlanet_thenThrowPlanetNotFoundException() {
        // Given
        String invalidId = "NOPE";

        // When / Then
        assertThrows(PlanetNotFoundException.class, () -> planetService.update(invalidId, "Ghost"));
    }

    @Test
    @Order(8)
    void givenNonExistingPlanetId_whenFindById_thenReturnEmptyOptional() {
        // Given
        String invalidId = "UNKNOWN";

        // When
        Optional<Planet> actualResult = planetService.findById(invalidId);

        // Then
        assertTrue(actualResult.isEmpty());
    }
}
