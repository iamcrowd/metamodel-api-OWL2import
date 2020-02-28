package com.gilia.owlimporter.importer.objectProperty;

import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.gilia.metamodel.*;
import com.gilia.owlimporter.importer.Importer;

import com.gilia.exceptions.OWLClassNotFoundException;

/**
 *
 * Import classes from an OWL 2 specification
 *
 * @author gbraun
 *
 */
public class OWLObjectProperty {
	
	public OWLObjectProperty() {
		
	}
	
	/**
	 * Import all OWL Object Properties into a KF instance with Relationships
	 * In order to import relationships, KF requires to define domain and range of each one.
	 * Domain and range are classes. After that for each one of theses classes and relatiships,
	 * the respective roles will be created (fresh names will be given for them)
	 * 
	 * @param kf a metamodel instance
	 * @param onto an OWLOntology being imported
	 */
	public static void owlObjectProperty2Relationship(Metamodel kf, OWLOntology onto) {
		Iterator<OWLObjectProperty> iteraop = onto.objectPropertiesInSignature().iterator();
		
		while (iteraop.hasNext()) {
			OWLObjectProperty anop = iteraclasses.next();
			String anop_iri = anop.toStringID();
			
			Relationship rel = new Relationship(anop_iri);
			kf.addRelationship(rel);
		}
	}
	
}