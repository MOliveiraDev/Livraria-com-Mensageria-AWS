package com.microsservice.catalog_service.dto;

public record BookRequestDTO(

        String title,
        String author,
        String isbn,
        Integer quantity
) {
}
