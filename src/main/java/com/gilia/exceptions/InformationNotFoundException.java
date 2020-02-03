package com.gilia.exceptions;

/**
 * Exception for handling the problem of not finding information that should already be in the Metamodel. Not to be confused with EntityNotValidException and AlreadyExistException.
 *
 * @see EntityNotValidException
 * @see AlreadyExistException
 */
public class InformationNotFoundException extends MetamodelException {
    public InformationNotFoundException(String message) {
        super("InformationNotFoundException - " + message);
    }
}
