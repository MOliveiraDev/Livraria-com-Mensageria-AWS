package com.microsservice.catalog_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookReturnedReceivedEventDTO(
        @JsonProperty("bookId") Long bookId,
        @JsonProperty ("email") String email,
        @JsonProperty ("bookName") String bookName

) {
}
