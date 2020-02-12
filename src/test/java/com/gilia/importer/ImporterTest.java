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
import com.gilia.owlimporter.Importer;
import com.gilia.owlimporter.OWLClasses;

/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class ImporterTest {

	@Test
    public void testCreateAnEmptyKFandLoadOntoFromFile() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1]);
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
    	  	Importer importer = new Importer(ontoiri);
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
    public void testObjectTypesFromOWL2() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1]);
    	  	importer.OWLClassesImport();
    	  	System.out.println(importer.toJSON());
    	//  	Metamodel meta = importer.getKFInstance();
    	//  	OWLClasses import_classes = new OWLClasses();
    	//  	import_classes.owlClasses2ObjectType(meta,importer);
    	//  	System.out.println(meta.toString());
    	//  	MetaBuilder builder = new MetaConverter();
    	//  	System.out.println(builder.generateJSON(meta));
    	  	//assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
    
}