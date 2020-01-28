package com.gilia.exceptions;

/**
 * Exception for handling the problem of finding an Entity that already exists in the Metamodel. Not to be confused with EntityNotValidException and InformationNotFoundException.
 *
 * @see EntityNotValidException
 * @see InformationNotFoundException
 */
public class AlreadyExistException extends MetamodelException {
    public AlreadyExistException(String message) {
        super("AlreadyExistException - " + message);
    }
}
