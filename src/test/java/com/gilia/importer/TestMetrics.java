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
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.metamodel.*;
import com.gilia.builder.metabuilder.*;
import com.gilia.owlimporter.importer.Importer;


/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class TestMetrics {
	
	@Test
    public void testMetricsForTime() {
    	try {
    		IRI ontoiri = IRI.create("http://www.w3.org/2006/time#");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();

    	  	importer.calculateMetrics();    	  	
			System.out.println("\tSize ontology (logical) axioms: " + importer.getNumberOfAx());
			System.out.println("\tSize ontology entities: " + importer.getNumberOfEntities());
			
			System.out.println("\tSize norm ontology (SUBCLASS_OF) axioms: " + importer.getNumberOfNormAx());
			System.out.println("\tSize norm ontology entities: " + importer.getNumberOfNormEntities());
			
			System.out.println("\tSize unsupported (logical) axioms: " + importer.getNumberOfNonNormAx());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testMetricsForPeople() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/ex-people.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();
    	  	
    	  	importer.calculateMetrics(); 
    	  	System.out.println("\tSize ontology (logical) axioms: " + importer.getNumberOfAx());
			System.out.println("\tSize ontology entities: " + importer.getNumberOfEntities());
			
			System.out.println("\tSize norm ontology (SUBCLASS_OF) axioms: " + importer.getNumberOfNormAx());
			System.out.println("\tSize norm ontology entities: " + importer.getNumberOfNormEntities());
			
			System.out.println("\tSize unsupported (logical) axioms: " + importer.getNumberOfNonNormAx());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
    
}