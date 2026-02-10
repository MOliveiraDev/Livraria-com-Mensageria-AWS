package com.microsservice.catalog_service.dto;

public record BookCatalogRequestDTO(

        String title,
        String author,
        String isbn,
        Integer quantity
) {
}
