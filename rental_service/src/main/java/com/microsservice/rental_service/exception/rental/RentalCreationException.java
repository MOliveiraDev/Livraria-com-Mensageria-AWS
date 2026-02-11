package com.microsservice.rental_service.exception.rental;

public class RentalCreationException extends RuntimeException {
    public RentalCreationException(String message) {
        super(message);
    }
}
