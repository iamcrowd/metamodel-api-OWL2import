package com.gilia.importer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.metamodel.*;
import com.gilia.builder.metabuilder.*;
import com.gilia.owlimporter.importer.Importer;


import com.gilia.owlimporter.importer.classExpression.Class;
import com.gilia.owlimporter.importer.axiom.classAxiom.SubClassOf;

/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class ImporterTest {

	@Test
    public void testCreateAnEmptyKFandLoadOntoFromFile() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	assertEquals("Number of Axioms", onto.getAxiomCount(), 801);
    	  	assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testCreateAnEmptyKFandLoadOntoFromIRI() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	assertTrue(onto.isOntology());
    	  	assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testImportAxiomsType1AasKF() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1AfromOntology();
    	  	System.out.println(importer.toJSON());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testImportAxiomsType1BasKF() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1BfromOntology();
    	  	System.out.println(importer.toJSON());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testImportAxiomsType1CasKF() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1CfromOntology();
    	  	System.out.println(importer.toJSON());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testImportAxiomsAllasKF() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();
    	  	System.out.println(importer.toJSON());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
    
}