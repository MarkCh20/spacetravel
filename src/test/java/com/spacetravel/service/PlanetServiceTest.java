package com.spacetravel.service;

import com.spacetravel.dao.PlanetDaoImpl;
import com.spacetravel.entity.Planet;
import com.spacetravel.exception.PlanetNotFoundException;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PlanetServiceTest {

    private static PlanetCrudServiceImpl service;
    private static final String PLANET_ID = "planet-001";

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

        service = new PlanetCrudServiceImpl(new PlanetDaoImpl());
    }


    @Test
    @Order(1)
    void givenPlanetIdAndName_whenCreatePlanet_thenPlanetIsPersisted() {
        // Given
        String planetName = "Earth 2";

        // When
        Planet actualPlanet = service.create(PLANET_ID, planetName);

        // Then
        assertEquals(PLANET_ID, actualPlanet.getId(), "Planet ID should match input");
        assertEquals(planetName, actualPlanet.getName(), "Planet name should match input");
    }

    @Test
    @Order(2)
    void givenExistingPlanetId_whenFindById_thenReturnPlanet() {
        // When
        Planet actualPlanet = service.findById(PLANET_ID);

        // Then
        assertNotNull(actualPlanet, "Planet should be found by existing ID");
        String expectedName = "Earth 2";
        String actualName = actualPlanet.getName();
        assertEquals(expectedName, actualName, "Planet name should be 'Earth 2'");
    }

    @Test
    @Order(3)
    void givenNonExistingPlanetId_whenFindById_thenReturnEmptyOptional() {
        // Given
        String invalidId = "invalid-planet";

        // Then
        assertThrows(PlanetNotFoundException.class,
                () -> service.findById(invalidId),
                "Finding non-existing planet should throw PlanetNotFoundException");
    }

    @Test
    @Order(4)
    void givenPlanetsExist_whenFindAll_thenReturnNonEmptyList() {
        // When
        List<Planet> actualPlanets = service.findAll();

        // Then
        assertFalse(actualPlanets.isEmpty(), "Planet list should not be empty");
    }

    @Test
    @Order(5)
    void givenExistingPlanetIdAndNewName_whenUpdatePlanet_thenPlanetNameIsUpdated() {
        // Given
        String updatedName = "New Earth";

        // When
        Planet actualUpdatedPlanet = service.update(PLANET_ID, updatedName);

        // Then
        assertEquals(updatedName, actualUpdatedPlanet.getName(), "Planet name should be updated");
    }

    @Test
    @Order(6)
    void givenNonExistingPlanetId_whenUpdatePlanet_thenThrowPlanetNotFoundException() {
        // Given
        String invalidId = "invalid-planet";
        String newName = "Name";

        // When & Then
        assertThrows(PlanetNotFoundException.class,
                () -> service.update(invalidId, newName),
                "Updating non-existing planet should throw PlanetNotFoundException");
    }

    @Test
    @Order(7)
    void givenExistingPlanetId_whenDeletePlanet_thenPlanetIsDeleted() {
        // When
        assertDoesNotThrow(() -> service.delete(PLANET_ID), "Deleting existing planet should not throw");

        // Then
        assertThrows(PlanetNotFoundException.class,
                () -> service.findById(PLANET_ID),
                "After deletion, planet should not be found and throw PlanetNotFoundException");
    }

    @Test
    @Order(8)
    void givenNonExistingPlanetId_whenDeletePlanet_thenThrowPlanetNotFoundException() {
        // Given
        String invalidId = "invalid-planet";

        // When & Then
        assertThrows(PlanetNotFoundException.class,
                () -> service.delete(invalidId),
                "Deleting non-existing planet should throw PlanetNotFoundException");
    }
}
