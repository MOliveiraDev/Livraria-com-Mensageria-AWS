package com.microsservice.catalog_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentalRequestDTO(
        @NotNull(message = "Book ID is required")
        Long bookId,
        
        @NotNull(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotNull(message = "Return date is required")
        LocalDate returnDate
) {
}