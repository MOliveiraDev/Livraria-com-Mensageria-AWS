package com.microsservice.rental_service.dto;

public record RentalCreatedEventDTO(
        Long bookId,
        String bookTitle,
        String email,
        String returnDate
) {
}
