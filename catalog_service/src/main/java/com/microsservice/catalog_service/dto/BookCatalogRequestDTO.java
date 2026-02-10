package com.microsservice.catalog_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookCatalogRequestDTO(

        @NotBlank(message = "Title is mandatory")
        String title,
        
        @NotBlank(message = "Author is mandatory")
        String author,
        
        @NotBlank(message = "ISBN is mandatory")
        String isbn,
        
        @NotNull(message = "Quantity is mandatory")
        Integer quantity
) {
}
