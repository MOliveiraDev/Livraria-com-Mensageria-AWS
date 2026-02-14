package com.microsservice.rental_service.dto;

import java.util.List;

public record BookReturnedCreatedEventDTO(
        List<Long> bookIds
) {
}
