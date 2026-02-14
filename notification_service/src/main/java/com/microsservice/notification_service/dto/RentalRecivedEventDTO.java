package com.microsservice.notification_service.dto;

public record RentalRecivedEventDTO(
        Long bookId,
        String bookTitle,
        String email,
        String returnDate
) {
}
