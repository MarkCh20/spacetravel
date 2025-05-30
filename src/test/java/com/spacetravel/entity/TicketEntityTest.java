package com.spacetravel.entity;

import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TicketEntityTest {

    @Test
    void givenTicket_whenSetFields_thenGettersReturnCorrectValues() {
        // Given
        Ticket ticket = new Ticket();
        Client client = new Client("John");
        Planet from = new Planet("EARTH", "Earth");
        Planet to = new Planet("MARS", "Mars");
        Instant createdAt = Instant.now();

        // When
        ticket.setId(42L);
        ticket.setClient(client);
        ticket.setFromPlanet(from);
        ticket.setToPlanet(to);
        ticket.setCreatedAt(createdAt);

        //Then
        assertEquals(42L, ticket.getId());
        assertEquals(client, ticket.getClient());
        assertEquals(from, ticket.getFromPlanet());
        assertEquals(to, ticket.getToPlanet());
        assertEquals(createdAt, ticket.getCreatedAt());
    }

    @Test
    void givenConstructor_whenCreateTicket_thenFieldsAreSetCorrectly() {
        //Given
        Client client = new Client("Alice");
        Planet from = new Planet("EARTH", "Earth");
        Planet to = new Planet("MARS", "Mars");

        //When
        Ticket ticket = new Ticket(client, from, to);

        //Then
        assertEquals(client, ticket.getClient());
        assertEquals(from, ticket.getFromPlanet());
        assertEquals(to, ticket.getToPlanet());
        assertNotNull(ticket.getCreatedAt());
    }

    @Test
    void givenTicketClass_whenCheckAnnotations_thenAllEntityAnnotationsPresentAndCorrect() throws NoSuchFieldException {
        // Given & When & Then

        // @Entity and @Table
        assertTrue(Ticket.class.isAnnotationPresent(Entity.class));
        assertTrue(Ticket.class.isAnnotationPresent(Table.class));
        assertEquals("ticket", Ticket.class.getAnnotation(Table.class).name());

        // @Id and @GeneratedValue on id
        Field idField = Ticket.class.getDeclaredField("id");
        assertTrue(idField.isAnnotationPresent(Id.class));
        assertTrue(idField.isAnnotationPresent(GeneratedValue.class));
        assertEquals(GenerationType.IDENTITY, idField.getAnnotation(GeneratedValue.class).strategy());

        // @Column on createdAt
        Field createdAtField = Ticket.class.getDeclaredField("createdAt");
        assertTrue(createdAtField.isAnnotationPresent(Column.class));
        assertFalse(createdAtField.getAnnotation(Column.class).nullable());
        assertEquals("created_at", createdAtField.getAnnotation(Column.class).name());

        // @ManyToOne + @JoinColumn on client
        Field clientField = Ticket.class.getDeclaredField("client");
        assertTrue(clientField.isAnnotationPresent(ManyToOne.class));
        assertTrue(clientField.isAnnotationPresent(JoinColumn.class));
        assertEquals("client_id", clientField.getAnnotation(JoinColumn.class).name());
        assertFalse(clientField.getAnnotation(JoinColumn.class).nullable());

        // @ManyToOne + @JoinColumn on fromPlanet
        Field fromPlanetField = Ticket.class.getDeclaredField("fromPlanet");
        assertTrue(fromPlanetField.isAnnotationPresent(ManyToOne.class));
        assertTrue(fromPlanetField.isAnnotationPresent(JoinColumn.class));
        assertEquals("from_planet_id", fromPlanetField.getAnnotation(JoinColumn.class).name());
        assertFalse(fromPlanetField.getAnnotation(JoinColumn.class).nullable());

        // @ManyToOne + @JoinColumn on toPlanet
        Field toPlanetField = Ticket.class.getDeclaredField("toPlanet");
        assertTrue(toPlanetField.isAnnotationPresent(ManyToOne.class));
        assertTrue(toPlanetField.isAnnotationPresent(JoinColumn.class));
        assertEquals("to_planet_id", toPlanetField.getAnnotation(JoinColumn.class).name());
        assertFalse(toPlanetField.getAnnotation(JoinColumn.class).nullable());

    }

}
