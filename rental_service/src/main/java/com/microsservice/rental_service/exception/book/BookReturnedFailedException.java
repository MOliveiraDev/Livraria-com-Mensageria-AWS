package com.microsservice.rental_service.exception.book;

public class BookReturnedFailedException extends RuntimeException {
    public BookReturnedFailedException(String message) {
        super(message);
    }
}
