package com.microsservice.catalog_service.exception;

import java.time.LocalDateTime;

public record ApiException(

        String message,
        String details,
        int statusCode,
        LocalDateTime timestamp
) {
}
