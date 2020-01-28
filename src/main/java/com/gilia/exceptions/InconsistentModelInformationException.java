package com.gilia.exceptions;

/**
 * Exception for handling the problem of having inconsistent information within the Metamodel or during JSON Translation/Conversion
 */
public class InconsistentModelInformationException extends MetamodelException {
    public InconsistentModelInformationException(String message) {
        super("InconsistentModelInformationException - " + message);
    }
}
