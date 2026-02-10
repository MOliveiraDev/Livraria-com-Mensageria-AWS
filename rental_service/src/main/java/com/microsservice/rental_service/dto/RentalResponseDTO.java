package com.microsservice.rental_service.dto;

import java.time.LocalDate;

public record RentalResponseDTO(
        Long bookId,
        String email,
        LocalDate returnDate,
        LocalDate rentalDate
) {
}
