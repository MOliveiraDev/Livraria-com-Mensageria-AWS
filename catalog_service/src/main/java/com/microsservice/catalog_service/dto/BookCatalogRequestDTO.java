package com.microsservice.catalog_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record BookCatalogRequestDTO(

        @NotBlank(message = "Title is mandatory")
        String title,
        
        @NotBlank(message = "Author is mandatory")
        String author,
        
        @NotBlank(message = "ISBN is mandatory")
        String isbn,
        
        @NotNull(message = "Quantity is mandatory")
        @PositiveOrZero(message = "Quantity must be zero or positive")
        Integer quantity
) {
}
