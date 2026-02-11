package com.microsservice.rental_service.exception;

import com.microsservice.rental_service.exception.rental.BookNotFoundException;
import com.microsservice.rental_service.exception.rental.RentalCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiException> handleBookNotFoundException(BookNotFoundException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                "Book not found",
                HttpStatus.NOT_FOUND.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiException);
    }

    @ExceptionHandler(RentalCreationException.class)
    public ResponseEntity<ApiException> handleRentalCreationException(RentalCreationException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                "Rental creation failed",
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(apiException);
    }
}
