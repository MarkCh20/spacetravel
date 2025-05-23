package com.spacetravel.exception;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(String message, String id) {
        super(message + id);
    }

    public PlanetNotFoundException(String id) {
        super("Planet with id " + id + " not found.");
    }

}
