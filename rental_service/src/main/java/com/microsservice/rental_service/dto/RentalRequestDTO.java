package com.microsservice.rental_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record RentalRequestDTO(
        @NotEmpty(message = "Book IDs list cannot be empty")
        List<Long> bookIds,
        
        @NotNull(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        
        @NotNull(message = "Return date is required")
        LocalDate returnDate
) {
}
