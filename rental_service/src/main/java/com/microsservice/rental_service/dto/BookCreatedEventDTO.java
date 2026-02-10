package com.microsservice.rental_service.dto;

public record BookCreatedEventDTO(
        Long bookId,
        String title,
        String status
) {
}
