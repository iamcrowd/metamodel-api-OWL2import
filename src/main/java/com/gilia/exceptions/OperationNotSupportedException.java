package com.gilia.exceptions;

/**
 * Exception for handling the problem of trying to perform actions not supported by the API or the Metamodel definition
 */
public class OperationNotSupportedException extends MetamodelException {
    public OperationNotSupportedException(String message) {
        super("OperationNotSupportedException - " + message);
    }
}
