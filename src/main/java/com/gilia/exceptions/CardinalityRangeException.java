package com.gilia.exceptions;

public class CardinalityRangeException extends MetamodelException {
    public CardinalityRangeException(String message) {
        super("CardinalityRangeException - " + message);
    }
}
