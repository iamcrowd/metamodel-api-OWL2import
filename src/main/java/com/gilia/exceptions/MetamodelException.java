package com.gilia.exceptions;

public class MetamodelException extends RuntimeException {
    public MetamodelException(String message) {
        super("MetamodelException - " + message);
    }
}
