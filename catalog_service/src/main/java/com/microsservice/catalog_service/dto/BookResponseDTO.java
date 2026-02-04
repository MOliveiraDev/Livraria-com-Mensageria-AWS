package com.microsservice.catalog_service.dto;

import com.microsservice.catalog_service.enums.BookStatus;

public record BookResponseDTO(
        Long bookId,
        String title,
        String author,
        String isbn,
        BookStatus status,
        Integer quantity,
        String message
) {
}
