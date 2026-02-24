package com.microsservice.rental_service.exception;

import com.microsservice.rental_service.exception.book.BookReturnedFailedException;
import com.microsservice.rental_service.exception.rental.BookNotFoundException;
import com.microsservice.rental_service.exception.rental.RentalCreationException;
import com.microsservice.rental_service.exception.rental.RentalNotFoundException;
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

    @ExceptionHandler(RentalNotFoundException.class)
    public ResponseEntity<ApiException> handleRentalNotFoundException(RentalNotFoundException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                "Rental not found",
                HttpStatus.NOT_FOUND.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiException);
    }

    @ExceptionHandler(BookReturnedFailedException.class)
    public ResponseEntity<ApiException> handleBookReturnedFailedException(BookReturnedFailedException e) {
        ApiException apiException = new ApiException(
                e.getMessage(),
                "Book returned failed",
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(apiException);
    }
}
