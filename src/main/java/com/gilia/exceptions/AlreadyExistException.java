package com.gilia.exceptions;

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String message) {
        super("AlreadyExistException - " + message);
    }
}
