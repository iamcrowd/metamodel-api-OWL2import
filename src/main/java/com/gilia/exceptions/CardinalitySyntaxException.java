package com.gilia.exceptions;

/**
 * Exception for handling the problem of a not valid (syntactically) cardinality string
 */
public class CardinalitySyntaxException extends MetamodelException {
    public CardinalitySyntaxException(String message) {
        super("CardinalitySyntaxException - " + message);
    }
}
