package com.microsservice.rental_service.dto;

import java.time.LocalDate;
import java.util.List;

public record RentalResponseDTO(
        List<String> title,
        String email,
        LocalDate returnDate,
        LocalDate rentalDate
) {
}
