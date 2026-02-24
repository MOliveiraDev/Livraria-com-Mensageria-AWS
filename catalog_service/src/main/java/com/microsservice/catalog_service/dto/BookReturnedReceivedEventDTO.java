package com.microsservice.catalog_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookReturnedReceivedEventDTO(
        Long bookId,
        String email,
        String bookTitle

) {
}
