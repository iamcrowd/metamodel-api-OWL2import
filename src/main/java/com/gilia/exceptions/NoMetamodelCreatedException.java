package com.gilia.exceptions;

public class NoMetamodelCreatedException extends RuntimeException {
    public NoMetamodelCreatedException(String message) {
        super("NoMetamodelCreatedException - " + message);
    }
}
