package com.gilia.exceptions;

/**
 * Exception for dealing with any kind of problem found related to the Metamodel. This class should be extended in order to make
 * a richer exception hierarchy with more informative messages
 */
public class MetamodelException extends RuntimeException {
    public MetamodelException(String message) {
        super("MetamodelException - " + message);
    }
}
