package com.gilia.exceptions;

/**
 * Exception for handling the problem of trying to perform actions associated to a non existence Metamodel
 */
public class NoMetamodelCreatedException extends MetamodelException {
    public NoMetamodelCreatedException(String message) {
        super("NoMetamodelCreatedException - " + message);
    }
}
