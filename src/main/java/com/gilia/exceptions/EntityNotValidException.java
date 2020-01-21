package com.gilia.exceptions;

public class EntityNotValidException extends RuntimeException {
    public EntityNotValidException(String message) {
        super("EntityNotValidException - " + message);
    }
}
