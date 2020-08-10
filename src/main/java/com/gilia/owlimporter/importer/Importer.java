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
import java.util.Iterator;
import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.owlimporter.importer.ClassExpressionTools;
import com.gilia.owlimporter.importer.axiom.classAxiom.SubClassOf;


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
	 * @param iri, a String containing an Ontology URI 
	 * @param precompute, true if you want reasoning over ontology before importing. Otherwise, false.
	 */
	public Importer(String filePath, Boolean precompute) {
		try {
			File file = new File(filePath);
			validateOWL(file);
			this.kfimported = new Metamodel();
			this.man = OWLManager.createOWLOntologyManager();
			this.onto = man.loadOntologyFromOntologyDocument(file);
			
			if (precompute) {
				ReasonerFactory factory = new ReasonerFactory();
				OWLReasoner reasoner = factory.createReasoner(this.onto);
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
				this.onto = reasoner.getRootOntology();
			}
			
			this.metabuilder = new MetaConverter();
		}
		catch (Exception e){
        	e.printStackTrace();
    	}
	}
	
	/**
	 * 
	 * @param iri, a String containing an Ontology URI 
	 * @param precompute, true if you want reasoning over ontology before importing. Otherwise, false.
	 */
	public Importer(IRI iri, Boolean precompute) {
		try {
			validateOWL(iri);
			this.kfimported = new Metamodel();
			this.man = OWLManager.createOWLOntologyManager();
			this.onto = man.loadOntologyFromOntologyDocument(iri);
	        
			if (precompute) {
				ReasonerFactory factory = new ReasonerFactory();
				OWLReasoner reasoner = factory.createReasoner(this.onto);
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
				this.onto = reasoner.getRootOntology();
			}
			
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
	
	public JSONObject showOntology() {
		JSONObject jsonAx = new JSONObject();
    	Iterator<OWLAxiom> axs = this.onto.axioms().iterator();
    	while (axs.hasNext()) {
    		OWLAxiom ax = axs.next();
    		jsonAx.put(ax.getAxiomType().toString(), ax.toString());
        }
    	return jsonAx;
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
	public void class2KF() {
   	  	ClassExpressionTools import_classes = new ClassExpressionTools();
   	  	import_classes.owlClasses(this.kfimported, this.onto);
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
   	  	SubClassOf import_subclasses = new SubClassOf();
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
   	  	SubClassOf import_subclasses = new SubClassOf();
	  	import_subclasses.owlSubClassAxiomForGivenOWLClass2Subsumptions(this.kfimported, this.onto, anIRI);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
}