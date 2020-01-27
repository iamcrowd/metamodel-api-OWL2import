package com.gilia.exceptions;

public class NoMetamodelCreatedException extends MetamodelException {
    public NoMetamodelCreatedException(String message) {
        super("NoMetamodelCreatedException - " + message);
    }
}
