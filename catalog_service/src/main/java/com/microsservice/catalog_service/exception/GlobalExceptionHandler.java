package com.microsservice.catalog_service.exception;

import com.microsservice.catalog_service.exception.book.ISBNWasExistingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ISBNWasExistingException.class)
    public ResponseEntity<ApiException> handlerISBNWasExisting(ISBNWasExistingException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                "IBNS already exists in the catalog",
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(apiException);
    }
}
