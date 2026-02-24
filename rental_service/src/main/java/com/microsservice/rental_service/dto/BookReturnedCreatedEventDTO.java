package com.microsservice.rental_service.dto;

public record BookReturnedCreatedEventDTO(
        Long bookId,
        String bookTitle,
        String email
) {
}
