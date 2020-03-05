package com.gilia.exceptions;

/**
 * Exception for handling the problem of not finding information that should already be in the Metamodel. Not to be confused with EntityNotValidException and AlreadyExistException.
 * This exception should be used mainly during JSON parsing time.
 * <p>
 * For example: If the method identifyRelationships with a null JSONArray, then this exception should be thrown.
 *
 * @see EntityNotValidException
 * @see AlreadyExistException
 */
public class InformationNotFoundException extends MetamodelException {
    public InformationNotFoundException(String message) {
        super("InformationNotFoundException - " + message);
    }
}
