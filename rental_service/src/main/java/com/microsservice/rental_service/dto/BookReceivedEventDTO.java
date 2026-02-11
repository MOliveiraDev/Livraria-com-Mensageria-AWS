package com.microsservice.rental_service.dto;

public record BookReceivedEventDTO(
        Long bookId,
        String title,
        String status
) {
}
