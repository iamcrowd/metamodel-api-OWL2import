package com.gilia.owlimporter.importer;

import com.gilia.metamodel.*;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Subsumption;
import static com.gilia.utils.Utils.getAlphaNumericString;
import com.google.common.base.CaseFormat;

import com.gilia.owlimporter.importer.classExpression.Class;

import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;

import com.gilia.owlimporter.importer.axiom.classAxiom.SubClassOf;


import com.gilia.exceptions.EmptyOntologyException;

import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.normalization.Normalization;
import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.normalization.NormalForm;


/**
 * This class identifies each normal form in ontology normalised being imported and creates the respective KF primitives
 * 
 * @see NormalForm from ontologyutils dependency
 * 
 * @author gbraun
 *
 */
public class NormalFormTools {
	
	/**
	 *  *      A TBox axiom in normal form can be of one of four types:
	 *         
	 *         Type 1: Subclass(atom or conjunction of atoms, atom or
	 *         disjunction of atoms)
	 *         Type 2: Subclass(atom, exists property atom)
	 *         Type 3: Subclass(atom, forall property atom)
	 *         Type 4: Subclass(exists property atom, atom)
	 *         
	 */
	
	public NormalFormTools() {
		
	}
	
	/**
	 * Import OWL Classes and generate a KF instance with the respective set of ObjectTypes
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void asKF (Metamodel kf, OWLOntology normalOnto) {
		
		Stream<OWLAxiom> tBoxAxioms = normalOnto.tboxAxioms(Imports.EXCLUDED);
		
		tBoxAxioms.forEach((ax) -> {
			try {
				
				if (NormalForm.isNormalFormTBoxAxiom(ax)) {
					OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
					OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
					
					System.out.println("************************************ LEFT");
					System.out.println(left.toString());
					System.out.println("************************************");
					
					System.out.println("************************************ RIGHT");
					System.out.println(right.toString());
					System.out.println("************************************");
					// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
					// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
					
						if (NormalForm.typeOneSubClassAxiom(left, right)) {
							System.out.println("************************************");
							System.out.println("Im type 1");
							System.out.println("************************************");
							
							if ((!left.toString().contains("FRESH#")) &&
									(!right.toString().contains("FRESH#"))) {
								
								System.out.println("Im type 1 (a)");
								
								this.type1asKF(kf, normalOnto, left, right);
								
							} else if ((left.toString().contains("FRESH#")) &&
									(!right.toString().contains("FRESH#"))) {
								
								System.out.println("Im type 1 (b)");
								System.out.println(left.toString().substring(left.toString().indexOf('[') + 1, 
																						  left.toString().indexOf(']')));
								
							} else if ((!left.toString().contains("FRESH#")) &&
									(right.toString().contains("FRESH#"))) {
								
								System.out.println("Im type 1 (c)");
								System.out.println(right.toString().substring(right.toString().indexOf('[') + 1, 
										right.toString().indexOf(']')));
								
							} else if ((left.toString().contains("FRESH#")) &&
									(right.toString().contains("FRESH#"))) {
								
								System.out.println("Im type 1 (d)");
								System.out.println(left.toString().substring(left.toString().indexOf('[') + 1, 
										left.toString().indexOf('[')));
								System.out.println(right.toString().substring(right.toString().indexOf('[') + 1, 
										right.toString().indexOf(']')));
								
							}
							
						}
						if (NormalForm.typeTwoSubClassAxiom(left, right)) {
							System.out.println("************************************");
							System.out.println("Im type 2");
							System.out.println("************************************");
						}
						if (NormalForm.typeThreeSubClassAxiom(left, right)) {
							System.out.println("************************************");
							System.out.println("Im type 3");
							System.out.println("************************************");
						}
						if (NormalForm.typeFourSubClassAxiom(left, right)) {
							System.out.println("************************************");
							System.out.println("Im type 4");
							System.out.println("************************************");
						}
				}
				else {
					System.out.println("Do nothing:" + ax.toString());
				}
			}
			catch (Exception f) {
					f.printStackTrace();
					System.out.println("Unsupported axioms:" + ax.toString());
			}
		
		});
	}
	
	
	/**
	 * Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
	 *  - A \sqsubseteq B or  (atom, atom)
	 *  - A \sqcap B \sqsubseteq C or (conjuction of atoms, atom)
	 *  - A \sqsubseteq B \sqcup C (atom, disjunction of atoms)
	 *  - A \sqcap B \sqsubseteq C \sqcup D (conjunction of atoms, disjunction of atoms)
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type1asKF (Metamodel kf, OWLOntology onto, OWLClassExpression left, OWLClassExpression right) {

		//A \sqsubseteq B or  (atom, atom)
		if ((left.isClassExpressionLiteral()) && (right.isClassExpressionLiteral())) {
			
			String left_iri = left.asOWLClass().toStringID();
			String right_iri = right.asOWLClass().toStringID();
			ObjectType ot_child = new ObjectType(left_iri);
			ObjectType ot_parent = new ObjectType(right_iri);
		
			if (onto.getOntologyID().getOntologyIRI().isPresent()){
				Subsumption sub = new Subsumption(
						onto.getOntologyID().getOntologyIRI().get().toString() + "/" + getAlphaNumericString(3), 
						ot_parent, 
						ot_child);
				
				kf.addRelationship(sub);
			}
			else {
				Subsumption sub = new Subsumption(
						getAlphaNumericString(3), 
						ot_parent, 
						ot_child);
				kf.addRelationship(sub);
			}
			
			//if (e.isOWLClass() || e.isTopEntity() || e.isBottomEntity();
		}
	}
	
/*	static boolean isConjunctionOfAtoms(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF)) {
			return false;
		}
		Set<OWLClassExpression> conjunctions = e.asConjunctSet();
		for (OWLClassExpression c : conjunctions) {
			if (!isAtom(c)) {
				return false;
			}
		}
		return true;
	}

	static boolean isDisjunctionOfAtoms(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF)) {
			return false;
		}
		Set<OWLClassExpression> disjunctions = e.asDisjunctSet();
		for (OWLClassExpression d : disjunctions) {
			if (!isAtom(d)) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	static boolean isExistentialOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
			return false;
		}

		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	static boolean isUniversalOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
			return false;
		}

		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}*/
	
}