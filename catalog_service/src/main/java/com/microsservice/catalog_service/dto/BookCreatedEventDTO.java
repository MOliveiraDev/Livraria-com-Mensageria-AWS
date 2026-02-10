package com.microsservice.catalog_service.dto;

import com.microsservice.catalog_service.enums.BookStatus;

public record BookCreatedEventDTO(
        Long bookId,
        String title,
        BookStatus status
) {
}
