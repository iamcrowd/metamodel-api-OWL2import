package com.gilia.owlimport;

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
			
			System.out.println("\tImporting Time (s): " + importer.getImportingTime());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testKFMetricsForTime() {
    	try {
    		IRI ontoiri = IRI.create("http://www.w3.org/2006/time#");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();
  	  	
			System.out.println("\tEntities: " + importer.getNofEntities());
			System.out.println("\tObject types: " + importer.getNofObjectTypes());
			
			System.out.println("\tAttributes: " + importer.getNofAttributes());
			System.out.println("\tSubsumptions: " + importer.getNofSubsumptions());
			System.out.println("\tDisjointness: " + importer.getNofDisjointC());
			System.out.println("\tCompleteness: " + importer.getNofCompletenessC());
			
			System.out.println("\tRoles: " + importer.getNofRoles());
			System.out.println("\tBinary Rels: " + importer.getNofBinaryRels());
			System.out.println("\tCardinalities: " + importer.getNofCardinalities());

    	  	importer.calculateMetrics();    	  	
			System.out.println("\tSize ontology (logical) axioms: " + importer.getNumberOfAx());
			System.out.println("\tSize ontology entities: " + importer.getNumberOfEntities());
			
			System.out.println("\tSize norm ontology (SUBCLASS_OF) axioms: " + importer.getNumberOfNormAx());
			System.out.println("\tSize norm ontology entities: " + importer.getNumberOfNormEntities());
			
			System.out.println("\tSize unsupported (logical) axioms: " + importer.getNumberOfNonNormAx());
			
			System.out.println("\tImporting Time (s): " + importer.getImportingTime());
			
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
			
			System.out.println("\tNumber of FRESH: " + importer.getNumberOfFresh());
			System.out.println("\tNumber of IMPORT: " + importer.getNumberOfImport());
			
			System.out.println("\tImporting Time (s): " + importer.getImportingTime());
			
			System.out.println("\tEntities: " + importer.getNofEntities());
			System.out.println("\tObject types: " + importer.getNofObjectTypes());
			
			System.out.println("\tAttributes: " + importer.getNofAttributes());
			System.out.println("\tSubsumptions: " + importer.getNofSubsumptions());
			System.out.println("\tDisjointness: " + importer.getNofDisjointC());
			System.out.println("\tCompleteness: " + importer.getNofCompletenessC());
			
			System.out.println("\tRoles: " + importer.getNofRoles());
			System.out.println("\tBinary Rels: " + importer.getNofBinaryRels());
			System.out.println("\tCardinalities: " + importer.getNofCardinalities());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testMetricsForODPs() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/trajectory.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();
    	  	
    	  	importer.calculateMetrics();
    	  	
    	  	System.out.println("\tSize ontology (logical) axioms: " + importer.getNumberOfAx());
			System.out.println("\tSize norm ontology (SUBCLASS_OF) axioms: " + importer.getNumberOfNormAx());

			System.out.println("\tNumber of Classes in orig: " + importer.getNumberOfClassesInOrig());
			System.out.println("\tNumber of Classes in norm: " + importer.getNumberOfClassesInNorm());
			
			System.out.println("\tNumber of Fresh: " + importer.getNumberOfFresh());
			
			System.out.println("\tSize unsupported (logical) axioms: " + importer.getNumberOfNonNormAx());
			
			System.out.println("\tImporting Time (s): " + importer.getImportingTime());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testMetricsForComplement() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/trajectory-mini.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importNormalisedOntology();
    	  	
    	  	importer.calculateMetrics();
    	  	
    	  	System.out.println("\tSize ontology (logical) axioms: " + importer.getNumberOfAx());
			System.out.println("\tSize norm ontology (SUBCLASS_OF) axioms: " + importer.getNumberOfNormAx());

			System.out.println("\tNumber of Classes in orig: " + importer.getNumberOfClassesInOrig());
			System.out.println("\tNumber of Classes in norm: " + importer.getNumberOfClassesInNorm());
			
			System.out.println("\tNumber of Fresh: " + importer.getNumberOfFresh());
			
			System.out.println("\tSize unsupported (logical) axioms: " + importer.getNumberOfNonNormAx());
			
			System.out.println("\tImporting Time (s): " + importer.getImportingTime());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	
    
}
