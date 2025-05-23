package com.spacetravel.dao;

import com.spacetravel.entity.Planet;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlanetDaoTest {

    private PlanetDaoImpl planetDao;

    @BeforeAll
    public void setUp() {
        System.setProperty("config.file", "application-test.properties");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false", "sa", "")
                .locations("filesystem:src/test/resources/db/migration")
                .load();
        flyway.migrate();

        planetDao = new PlanetDaoImpl();
    }


    @Test
    public void givenNewPlanet_whenSave_thenPlanetIsPersisted() {
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
    public void givenExistingPlanet_whenUpdate_thenPlanetNameIsUpdated() {
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
    public void givenExistingPlanet_whenDelete_thenPlanetIsRemoved() {
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
    public void givenMultiplePlanets_whenFindAll_thenReturnListOfPlanets() {
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
}
