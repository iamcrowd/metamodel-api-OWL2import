package com.gilia.exceptions;

public class InconsistentModelInformationException extends MetamodelException {
    public InconsistentModelInformationException(String message) {
        super("InconsistentModelInformationException - " + message);
    }
}
