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
    public void testOnlyImport1AasKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1a.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1AfromOntology();
    	  	
    	  	System.out.println(importer.toJSON());
    	  	
    	  	/*Path fileName = Path.of(new String(ImporterTest.class.getClassLoader().getResource("metamodels/1a.json").toString()));
    	  	String actual = Files.readString(fileName);
    	  	assertEquals("Normalised Axiom 1A", importer.toJSON(), actual);	*/
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testOnlyImport1BasKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1b.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
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
    public void testOnlyImport1CasKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1c.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
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
    public void testOnlyImport1DasKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1d.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1DfromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testOnlyImport2asKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2asKFrepeted() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-repeted.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2MinCardAsKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-mincard.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2MinCardMAsKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-mincardm.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2MaxCardAsKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-maxcard.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2MaxCardMAsKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-maxcardm.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }

	@Test
    public void testOnlyImport2ExactCardAsKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2-exactcard.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testOnlyImport3asKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/3.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType3fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testOnlyImport4asKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/4.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType4fromOntology();
    	  	System.out.println(importer.toJSON());
    	  	
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
    public void testImportAxiomsType1DasKF() {
    	try {
    		IRI ontoiri = IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType1DfromOntology();
    	  	System.out.println(importer.toJSON());
    	}
    	catch (Exception e){
        	e.printStackTrace();
    	}
    }
	
	@Test
    public void testImportAxiomsType2asKF() {
    	try {
    		IRI ontoiri = IRI.create("http://www.w3.org/2006/time#");
    	  	Importer importer = new Importer(ontoiri,true);
    	  	Metamodel meta = importer.getKFInstance();
    	  	OWLOntology onto = importer.getOntology();
    	  	importer.importType2fromOntology();
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

	@Test
    public void testOnlyImportPeopleasKF() {
    	try {
    		String path = new String(ImporterTest.class.getClassLoader().getResource("ontologies/ex-people.owl").toString());
    		String[] owlfilepath = path.split(":", 2);
    	  	Importer importer = new Importer(owlfilepath[1],true);
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
