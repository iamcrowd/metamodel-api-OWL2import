package com.gilia.exceptions;

/**
 * Exception for handling the problem of a not valid Entity reference. Not to be confused with AlreadyExistException and InformationNotFoundException.
 *
 * @see AlreadyExistException
 * @see InformationNotFoundException
 */
public class EntityNotValidException extends MetamodelException {
    public EntityNotValidException(String message) {
        super("EntityNotValidException - " + message);
    }
}
