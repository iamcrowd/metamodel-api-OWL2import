package com.gilia.exceptions;

/**
 *
 */
public class NotValidLanguageException extends RuntimeException {
    public NotValidLanguageException(String message) {
        super("NotValidLanguageException - " + message);
    }
}