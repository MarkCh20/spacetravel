package com.spacetravel.exception;

public class PlanetNotFoundException extends RuntimeException {
    public PlanetNotFoundException(String id) {
        super("Planet not found with ID: " + id);
    }
}
