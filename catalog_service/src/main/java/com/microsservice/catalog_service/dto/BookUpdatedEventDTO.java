package com.microsservice.catalog_service.dto;

import com.microsservice.catalog_service.enums.BookStatus;

public record BookUpdatedEventDTO(
        Long bookId,
        String title,
        BookStatus status
) {
}
