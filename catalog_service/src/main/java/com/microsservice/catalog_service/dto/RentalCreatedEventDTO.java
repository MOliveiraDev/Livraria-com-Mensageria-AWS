package com.microsservice.catalog_service.dto;

public record RentalCreatedEventDTO(
        Long bookId,
        String email,
        String returnDate
) {
}
