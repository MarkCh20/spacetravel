package com.spacetravel.dao;

import com.spacetravel.entity.Planet;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlanetDaoTest {

    private PlanetDaoImpl planetDao;

    @BeforeAll
    void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();
        flyway.migrate();

        planetDao = new PlanetDaoImpl();
    }


    @Test
    void givenNewPlanet_whenSave_thenPlanetIsPersisted() {
        // Given
        Planet newPlanet = new Planet();
        newPlanet.setId("MAR");
        newPlanet.setName("Mars");

        // When
        planetDao.save(newPlanet);

        // Then
        Planet actualPlanet = planetDao.findById("MAR").orElse(null);
        assertNotNull(actualPlanet);
        String expectedName = "Mars";
        String actualName = actualPlanet.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void givenExistingPlanet_whenUpdate_thenPlanetNameIsUpdated() {
        // Given
        Planet planet = new Planet();
        planet.setId("VEN");
        planet.setName("Venus");
        planetDao.save(planet);

        String expectedName = "Venus Updated";

        // When
        planet.setName(expectedName);
        planetDao.update(planet);

        // Then
        Planet actualPlanet = planetDao.findById("VEN").orElse(null);
        assertNotNull(actualPlanet);
        String actualName = actualPlanet.getName();
        assertEquals(expectedName, actualName);
    }

    @Test
    void givenExistingPlanet_whenDelete_thenPlanetIsRemoved() {
        // Given
        Planet planetToDelete = new Planet();
        planetToDelete.setId("JUP");
        planetToDelete.setName("Jupiter");
        planetDao.save(planetToDelete);

        // When
        planetDao.delete(planetToDelete);

        // Then
        Planet actualPlanet = planetDao.findById("JUP").orElse(null);
        assertNull(actualPlanet);
    }

    @Test
    void givenMultiplePlanets_whenFindAll_thenReturnListOfPlanets() {
        // Given
        Planet p1 = new Planet();
        p1.setId("EAR");
        p1.setName("Earth");
        Planet p2 = new Planet();
        p2.setId("SAT");
        p2.setName("Saturn");
        planetDao.save(p1);
        planetDao.save(p2);

        // When
        List<Planet> actualPlanets = planetDao.findAll();

        // Then
        assertTrue(actualPlanets.size() >= 2);
    }

    @Test
    void givenSavedClient_whenFindById_thenCorrectClientIsReturned() {
        // Given
        Planet savedPlanet = new Planet("MARX", "MarsX");
        planetDao.save(savedPlanet);
        String planetId = savedPlanet.getId();

        // When
        Planet actualPlanet = planetDao.findById(planetId).orElse(null);

        // Then
        assertNotNull(actualPlanet, "Planet should be found by ID");
        assertEquals(savedPlanet.getName(), actualPlanet.getName(), "Planet name should match the saved one");
        assertEquals(planetId, actualPlanet.getId(), "Planet ID should match the saved one");
    }


    @Test
    void givenSavedPlanet_whenFindByName_thenCorrectPlanetIsReturned() {
        // Given
        Planet planet = new Planet();
        planet.setId("NEP");
        planet.setName("Neptune");
        planetDao.save(planet);

        // When
        Planet actualPlanet = planetDao.findByName("Neptune").orElse(null);

        // Then
        assertNotNull(actualPlanet, "Planet should be found by name");
        assertEquals("NEP", actualPlanet.getId(), "Planet ID should match the saved one");
        assertEquals("Neptune", actualPlanet.getName(), "Planet name should match the saved one");
    }
}
