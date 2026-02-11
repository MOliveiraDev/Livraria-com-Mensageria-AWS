package com.microsservice.rental_service.dto;

public record RentalCreatedEventDTO(
        Long bookId,
        String email,
        String returnDate
) {
}
