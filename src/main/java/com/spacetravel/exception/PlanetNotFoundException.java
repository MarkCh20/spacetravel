package com.spacetravel.exception;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(String message, String id) {
        super(message + id);
    }

    public PlanetNotFoundException(String ignoredMessage) {
        // Constructor for PlanetNotFoundException forId and forName
    }

    public static PlanetNotFoundException forId(String id) {
        return new PlanetNotFoundException("Planet with id '" + id + "' not found.");
    }

    public static PlanetNotFoundException forName(String name) {
        return new PlanetNotFoundException("Planet with name '" + name + "' not found.");
    }
}
