package com.gilia.exceptions;

/**
 * Exception for dealing with any kind of problem found related to the Importer. This class should be extended in order to make
 * a richer exception hierarchy with more informative messages
 */
public class OWLClassNotFoundException extends RuntimeException {
    public OWLClassNotFoundException(String message) {
        super("OWLClassNotFoundException - " + message);
    }
}
