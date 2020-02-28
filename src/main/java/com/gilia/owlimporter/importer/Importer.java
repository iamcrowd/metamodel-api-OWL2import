package com.gilia.owlimporter.importer;

import com.gilia.metamodel.*;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.owlimporter.importer.entity.OWLClasses;
import com.gilia.owlimporter.importer.axiom.classExpressionAxiom.OWLSubClassAxiom;


import com.gilia.exceptions.EmptyOntologyException;


/**
 * An importer is a KF metamodel instance of an OWL 2 specification
 * 
 * @author gbraun
 *
 */
public class Importer {
	
	private Metamodel kfimported;
	private MetaConverter metabuilder;
	private OWLOntology onto;
	private OWLOntologyManager man;
	
	/**
	 * 
	 * @param filePath
	 */
	public Importer(String filePath) {
		try {
			File file = new File(filePath);
			validateOWL(file);
			this.kfimported = new Metamodel();
			this.man = OWLManager.createOWLOntologyManager();
	        ReasonerFactory factory = new ReasonerFactory();
            OWLReasoner reasoner = factory.createReasoner(man.loadOntologyFromOntologyDocument(file));
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
	        this.onto = reasoner.getRootOntology();
			this.metabuilder = new MetaConverter();
		}
		catch (Exception e){
        	e.printStackTrace();
    	}
	}
	
	/**
	 * 
	 * @param iri
	 */
	public Importer(IRI iri) {
		try {
			validateOWL(iri);
			this.kfimported = new Metamodel();
			this.man = OWLManager.createOWLOntologyManager();
	        
	        ReasonerFactory factory = new ReasonerFactory();
            OWLReasoner reasoner = factory.createReasoner(man.loadOntologyFromOntologyDocument(iri));
            reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
	        this.onto = reasoner.getRootOntology();
			this.metabuilder = new MetaConverter();
		}
		catch (Exception e){
        	e.printStackTrace();
		}
	}
	
	public Metamodel getKFInstance() {
		return this.kfimported;
	}
	
	public OWLOntologyManager getOntologyManager() {
		return this.man;
	}
	
	public OWLOntology getOntology() {
		return this.onto;
	}
	
	/**
	 * Metamodel instance is to exported as a JSONObject according to the KF JSON Scheme
	 * 
	 * @return JSONObject KF metamodel
	 */
	public JSONObject toJSON() {
		return this.metabuilder.generateJSON(this.kfimported);
	}
	
	
	/**
	 * Import OWL Classes and generate a KF instance with the respective set of ObjectTypes
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void OWLClassesImport() {
   	  	OWLClasses import_classes = new OWLClasses();
	  	import_classes.owlClasses2ObjectType(this.kfimported,this.onto);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
	/**
	 * Import All SubClasses and generate a KF instance with the respective set of ObjectTypes and
	 * Subsumptions
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void OWLSubClassesImport() {
   	  	OWLSubClassAxiom import_subclasses = new OWLSubClassAxiom();
	  	import_subclasses.owlSubClassAxiom2Subsumptions(this.kfimported,this.onto);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
	/**
	 * Import All SubClasses for a given Class and generate a KF instance with the respective set of ObjectTypes and
	 * Subsumptions
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void OWLSubClassesImport(IRI anIRI) {
   	  	OWLSubClassAxiom import_subclasses = new OWLSubClassAxiom();
	  	import_subclasses.owlSubClassAxiomForGivenOWLClass2Subsumptions(this.kfimported, this.onto, anIRI);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
}