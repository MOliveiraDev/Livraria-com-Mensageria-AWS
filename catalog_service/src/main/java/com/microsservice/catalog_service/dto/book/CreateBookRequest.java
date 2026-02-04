package com.microsservice.catalog_service.dto.book;

public record CreateBookRequest(

        String title,
        String author,
        String isbn,
        Integer quantity
) {
}
