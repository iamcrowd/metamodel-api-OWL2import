package com.gilia.exceptions;

public class EntityNotValidException extends MetamodelException {
    public EntityNotValidException(String message) {
        super("EntityNotValidException - " + message);
    }
}
