package com.microsservice.catalog_service.dto;

public record RentalRecivedEventDTO(
        Long bookId,
        String email,
        String returnDate
) {
}
