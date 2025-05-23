package com.spacetravel.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message, Long id) {
        super(message + id);
    }

    public ClientNotFoundException(Long id) {
        super("Client with id " + id + " not found.");
    }
}
