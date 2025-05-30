package com.spacetravel.entity;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class PlanetEntityTest {


    @Test
    void givenPlanet_whenSetIdAndName_thenGettersReturnCorrectValues() {
        // Given
        Planet planet = new Planet();
        planet.setId("MARS");
        planet.setName("Mars");

        // When
        String actualId = planet.getId();
        String actualName = planet.getName();

        // Then
        assertEquals("MARS", actualId);
        assertEquals("Mars", actualName);
    }


    @Test
    void givenIdAndNameInConstructor_whenCreatePlanet_thenFieldsAreSetCorrectly() {
        // Given
        String expectedId = "VENUS";
        String expectedName = "Venus";

        // When
        Planet planet = new Planet(expectedId, expectedName);

        // Then
        assertEquals(expectedId, planet.getId());
        assertEquals(expectedName, planet.getName());
    }


    @Test
    void givenPlanetClass_whenCheckAnnotations_thenAllEntityAnnotationsPresentAndCorrect() throws NoSuchFieldException {
        // Given & When & Then

        // @Entity and @Table
        assertTrue(Planet.class.isAnnotationPresent(Entity.class));
        assertTrue(Planet.class.isAnnotationPresent(Table.class));
        assertEquals("planet", Planet.class.getAnnotation(Table.class).name());

        // @Id and @Column on id Field
        Field idField = Planet.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(Column.class));
        Column idColumn = idField.getAnnotation(Column.class);
        assertEquals(10, idColumn.length());

        // @Column on name field
        Field nameField = Planet.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(Column.class));
        Column nameColumn = nameField.getAnnotation(Column.class);
        assertFalse(nameColumn.nullable());
        assertEquals(500, nameColumn.length());
    }
}
