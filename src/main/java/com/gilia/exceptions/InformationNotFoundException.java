package com.gilia.exceptions;

public class InformationNotFoundException extends MetamodelException {
    public InformationNotFoundException(String message) {
        super("InformationNotFoundException - " + message);
    }
}
