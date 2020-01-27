package com.gilia.exceptions;

public class CardinalitySyntaxException extends MetamodelException {
    public CardinalitySyntaxException(String message) {
        super("CardinalitySyntaxException - " + message);
    }
}
