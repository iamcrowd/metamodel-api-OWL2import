package com.gilia.owlimporter.importer.entity;

import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;

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
public class OWLClasses {
	
	public OWLClasses() {
		
	}
	
	public static void owlClasses2ObjectType(Metamodel kf, OWLOntology onto) {
		Iterator<OWLClass> iteraclasses = onto.classesInSignature().iterator();
		
		while (iteraclasses.hasNext()) {
			OWLClass anclass = iteraclasses.next();
			String anclass_iri = anclass.toStringID();
			ObjectType ot = new ObjectType(anclass_iri);
			kf.addEntity(ot);
		}
	}
	
}