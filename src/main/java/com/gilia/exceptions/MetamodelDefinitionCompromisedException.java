package com.gilia.exceptions;

public class MetamodelDefinitionCompromisedException extends MetamodelException {
    public MetamodelDefinitionCompromisedException(String message) {
        super("MetamodelDefinitionCompromisedException - " + message);
    }
}
