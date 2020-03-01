package com.gilia.owlimporter.importer.axiom.objectPropertyAxiom;

import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Subsumption;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import com.gilia.owlimporter.importer.Importer;
import com.gilia.owlimporter.importer.axiom.ClassAxiom;
import com.gilia.owlimporter.importer.axiom.ObjectPropertyAxiom;

import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 *
 * Import classes from an OWL 2 specification
 *
 * @author gbraun
 *
 */
public class ObjectPropertyDomain extends ObjectPropertyAxiom {
	
	public ObjectPropertyDomain() {
		
	}
	
	/**
	 * Import the Domain <OWLClassExpression> of a given ObjectProperty
	 *   
	 * @param kf
	 * @param onto
	 */
/**	public static void owlObjectPropertyDomain(Metamodel kf, 
											   OWLOntology onto,
											   OWLObjectProperty op) {
		
		Iterator<OWLObjectPropertyDomainAxiom> iteradomains = onto.objectPropertyDomainAxioms(op).
																   iterator();

		while (iteradomains.hasNext()) {
			OWLObjectPropertyDomainAxiom anaxiom = iteradomains.next();
			
			String anclass_iri = anclass.toStringID();
			ObjectType ot = new ObjectType(anclass_iri);
			kf.addEntity(ot);
			
			Iterator<OWLSubClassOfAxiom> iterasubsax = onto.subClassAxiomsForSuperClass(anclass).iterator();
			
			while (iterasubsax.hasNext()) {
				OWLSubClassOfAxiom ansubsax = iterasubsax.next();
				Iterator<OWLClass> iteraclassinax = ansubsax.classesInSignature().iterator();
				
				while (iteraclassinax.hasNext()) {
					OWLClass anclasschild = iteraclassinax.next();
					
					if (!anclasschild.equals(anclass)){
						String anclasschild_iri = anclasschild.toStringID();
						ObjectType ot_child = new ObjectType(anclasschild_iri);
						kf.addEntity(ot_child);
					
						if (onto.getOntologyID().getOntologyIRI().isPresent()){
							Subsumption sub = new Subsumption(
									onto.getOntologyID().getOntologyIRI().get().toString() + "/" + getAlphaNumericString(3), 
									ot, ot_child);
							kf.addRelationship(sub);
						}
						else {
							Subsumption sub = new Subsumption(
									getAlphaNumericString(3), 
									ot, ot_child);
							kf.addRelationship(sub);
						}
					}
				}
			}
		}
	} **/
	
	/**
	 * Imports all the subclass axioms for an <OWLClass> super given as parameter 
	 *   
	 * @param kf
	 * @param onto
	 */
/**	public static void owlSubClassAxiomForGivenOWLClass2Subsumptions(Metamodel kf, 
			OWLOntology onto, 
			IRI superclsIRI) {

		if (onto.containsClassInSignature(superclsIRI)) {
			OWLDataFactory odf = onto.getOWLOntologyManager().getOWLDataFactory();
			OWLClass supercls = odf.getOWLClass(superclsIRI);

			ObjectType ot = new ObjectType(superclsIRI.toString());
			kf.addEntity(ot);
			
			Iterator<OWLSubClassOfAxiom> iterasubsax = onto.subClassAxiomsForSuperClass(supercls).
															iterator();
			
			while (iterasubsax.hasNext()) {
				OWLSubClassOfAxiom ansubsax = iterasubsax.next();
				Iterator<OWLClass> iteraclassinax = ansubsax.classesInSignature().iterator();
				
				while (iteraclassinax.hasNext()) {
					OWLClass anclasschild = iteraclassinax.next();
					
					if (!anclasschild.equals(supercls)){
						String anclasschild_iri = anclasschild.toStringID();
						ObjectType ot_child = new ObjectType(anclasschild_iri);
						kf.addEntity(ot_child);

						if (onto.getOntologyID().getOntologyIRI().isPresent()){
							Subsumption sub = new Subsumption(
									onto.getOntologyID().getOntologyIRI().get().toString() + "/" + getAlphaNumericString(3), 
									ot, ot_child);
							kf.addRelationship(sub);
						}
						else {
							Subsumption sub = new Subsumption(
									getAlphaNumericString(3), 
									ot, ot_child);
							kf.addRelationship(sub);
						}
						
					}
				}
			}
		}
	}*/
	
}