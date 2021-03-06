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

import com.gilia.owlimporter.importer.entity.OWLClasses;
import com.gilia.owlimporter.importer.axiom.classExpressionAxiom.OWLSubClassAxiom;

/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class OWLSubClassAxiomTest {

	@Test
    public void testSubsumptionsFromOWL2() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	importer.OWLSubClassesImport();
    	  	System.out.println(importer.toJSON());
    	  	//assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testSubsumptionsFromOWL2Gufo() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/gufo.ttl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	importer.OWLSubClassesImport();
    	  	System.out.println(importer.toJSON());
    	  	//assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testSubsumptionsForGivenClassFromOWL2() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	
    	  	IRI IOR = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#MeatTopping");
    	  	importer.OWLSubClassesImport(IOR);
    	  	System.out.println(importer.toJSON());
    	  	//assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testSubsumptionsForGivenClassFromOWL2Gufo() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/gufo.ttl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	
    	  	IRI IOR = IRI.create("http://purl.org/nemo/gufo#Event");
    	  	importer.OWLSubClassesImport(IOR);
    	  	System.out.println(importer.toJSON());
    	  	//assertEquals("Metamodel empty", meta.toString(), "Metamodel{entities=[], relationships=[], roles=[], constraints=[]}");
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
    
}