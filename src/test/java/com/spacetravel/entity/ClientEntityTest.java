package com.spacetravel.entity;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class ClientEntityTest {


    @Test
    public void givenClient_whenSetIdAndName_thenGettersReturnCorrectValues() {
        // Given
        Client client = new Client();

        // When
        client.setId(1L);
        client.setName("John Doe");

        // Then
        Long expectedId = 1L;
        String expectedName = "John Doe";

        assertEquals(expectedId, client.getId());
        assertEquals(expectedName, client.getName());
    }


    @Test
    public void givenNameInConstructor_whenCreateClient_thenNameIsSetCorrectly() {
        // Given
        String expectedName = "Alice";

        // When
        Client client = new Client(expectedName);

        // Then
        assertEquals(expectedName, client.getName());
    }



    @Test
    public void givenClientClass_whenCheckAnnotations_thenAllEntityAnnotationsPresentAndCorrect() throws NoSuchFieldException {
        // Given & When & Then
        assertTrue(Client.class.isAnnotationPresent(Entity.class));
        assertTrue(Client.class.isAnnotationPresent(Table.class));
        assertEquals("client", Client.class.getAnnotation(Table.class).name());

        Field idField = Client.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));

        Field nameField = Client.class.getDeclaredField("name");
        assertTrue(nameField.isAnnotationPresent(Column.class));

        Column nameColumn = nameField.getAnnotation(Column.class);
        assertFalse(nameColumn.nullable());
        assertEquals(200, nameColumn.length());
    }
}