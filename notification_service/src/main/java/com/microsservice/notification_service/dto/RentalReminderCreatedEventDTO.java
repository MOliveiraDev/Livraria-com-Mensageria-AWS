package com.microsservice.notification_service.dto;

public record RentalReminderCreatedEventDTO (
        String email,
        String message,
        String returnDate
) {
}