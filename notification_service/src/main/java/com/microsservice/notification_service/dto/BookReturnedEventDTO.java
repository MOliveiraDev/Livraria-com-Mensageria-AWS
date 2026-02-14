package com.microsservice.notification_service.dto;

public record BookReturnedEventDTO(
        Long bookId,
        String bookTitle,
        String email
) {
}
