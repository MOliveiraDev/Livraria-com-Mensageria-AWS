package com.microsservice.catalog_service.exception.book;

public class ISBNWasExistingException extends RuntimeException {
    public ISBNWasExistingException(String message) {
        super(message);
    }
}
