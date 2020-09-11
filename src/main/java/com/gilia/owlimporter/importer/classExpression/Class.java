package com.gilia.owlimporter.importer.classExpression;

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

import com.gilia.owlimporter.importer.classExpression.ClassExpression;

import com.gilia.exceptions.OWLClassNotFoundException;

/**
 *
 * Import classes from an OWL 2 specification
 *
 * @author gbraun
 *
 */
public class Class extends ClassExpression{
	
	public Class() {
		
	}
	
	/**
	 * OWL Classes into a KF instance with Object types
	 * 
	 * @param kf a metamodel instance
	 * @param onto an OWLOntology being imported
	 * @param anclass an <OWLClass>
	 */
	public static void owlClasses2ObjectType(Metamodel kf, 
											 OWLOntology onto, 
											 OWLClass anclass) {
			String anclass_iri = anclass.toStringID();
			ObjectType ot = new ObjectType(anclass_iri);
			kf.addEntity(ot);
	}
	
}