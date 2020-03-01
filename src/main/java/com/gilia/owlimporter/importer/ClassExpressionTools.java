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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.owlimporter.importer.classExpression.Class;
import com.gilia.owlimporter.importer.axiom.classAxiom.SubClassOf;


import com.gilia.exceptions.EmptyOntologyException;


/**
 * An importer is a KF metamodel instance of an OWL 2 specification
 * 
 * @author gbraun
 *
 */
public class ClassExpressionTools {
	
	public ClassExpressionTools() {
		
	}
	
	/**
	 * Import OWL Classes and generate a KF instance with the respective set of ObjectTypes
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void owlClasses(Metamodel kf, OWLOntology onto) {
		Iterator<OWLClass> iteraclasses = onto.classesInSignature().iterator();
		
		while (iteraclasses.hasNext()) {
			OWLClass anclass = iteraclasses.next();
			Class classtoot = new Class();
			classtoot.owlClasses2ObjectType(kf, onto, anclass);
		}
	}
	
}