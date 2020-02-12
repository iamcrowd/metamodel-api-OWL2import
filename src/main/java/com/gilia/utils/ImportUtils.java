package com.gilia.utils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Random;

import static com.gilia.utils.Constants.CHARSET;
import static com.gilia.utils.Constants.RANDOM_STRING_REGEX;

import com.gilia.exceptions.EmptyOntologyException;


/**
 * Class use for utility methods that can be used anywhere in the application. The methods inside this class
 * are public and static, and will facilitate coding complex methods/functions
 */
public class ImportUtils {

    /**
     * Validates the given OWL 2 spec defined in an OWL file. The path to this file
     * must be provided as a parameter. The method will execute successfully (no exceptions will be thrown)
     * if the OWL 2 file is valid according to the W3C standard. If the OWL 2 spec is not valid, then it will
     * throw a ValidationException.
     * File path is given as this example: "C:\\pizza.owl.xml"
     *
     * @param owl2FilePath The path to a file that contains an OWL spec (OWL/XML | RDF/XML)
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateOWL(File owl2File) throws FileNotFoundException, EmptyOntologyException {
    	try {
        	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        	OWLOntology o = man.loadOntologyFromOntologyDocument(owl2File);
            if (o.isEmpty()) {
            	throw new EmptyOntologyException("Ontology given is empty");
            }
        }
        catch (Exception e){
        	e.printStackTrace();
        }
    }
    
    /**
     * Validates the given OWL 2 spec defined in an OWL file. The path to this file
     * must be provided as a parameter. The method will execute successfully (no exceptions will be thrown)
     * if the OWL 2 file is valid according to the W3C standard. If the OWL 2 spec is not valid, then it will
     * throw a ValidationException.
     * File path is given as this example: "C:\\pizza.owl.xml"
     *
     * @param owl2FilePath The path to a file that contains an OWL spec (OWL/XML | RDF/XML)
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateOWL(IRI owl2IRI) throws FileNotFoundException, EmptyOntologyException {
    	try {
        	OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        	OWLOntology o = man.loadOntologyFromOntologyDocument(owl2IRI);
            if (o.isEmpty()) {
            	throw new EmptyOntologyException("Ontology given is empty");
            }
        }
        catch (Exception e){
        	e.printStackTrace();
        }
    }
    
}