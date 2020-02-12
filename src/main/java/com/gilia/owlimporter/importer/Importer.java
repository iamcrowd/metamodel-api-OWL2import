package com.gilia.owlimporter;

import com.gilia.metamodel.*;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;

import com.gilia.exceptions.EmptyOntologyException;


/**
 * An importer is a KF metamodel instance of an OWL 2 specification
 * 
 * @author gbraun
 *
 */
public class Importer {
	
	private Metamodel kfimported;
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
	        this.onto = man.loadOntologyFromOntologyDocument(file);
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
	        this.onto = man.loadOntologyFromOntologyDocument(iri);
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
	
//	public void addOWLClasses(ArrayList<EntityType> classes) {
//		this.kfimported.setEntities(classes);
//	}
	
}