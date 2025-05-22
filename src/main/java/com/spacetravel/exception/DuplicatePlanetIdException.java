package com.spacetravel.exception;

public class DuplicatePlanetIdException extends RuntimeException {
    public DuplicatePlanetIdException(String message) {
        super(message);
    }
}
