package com.spacetravel.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(String message, Long id) {
        super(message + id);
    }

    public TicketNotFoundException(Long id) {
        super("Ticket with id " + id + " not found.");
    }

    public TicketNotFoundException(String message, String id) {
        super(message + id);
    }
}
