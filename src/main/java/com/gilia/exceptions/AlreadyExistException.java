package com.gilia.exceptions;

public class AlreadyExistException extends MetamodelException {
    public AlreadyExistException(String message) {
        super("AlreadyExistException - " + message);
    }
}
