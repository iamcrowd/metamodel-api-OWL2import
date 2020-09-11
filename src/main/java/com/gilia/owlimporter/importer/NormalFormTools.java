package com.gilia.owlimporter.importer;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.CompletenessConstraint;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
	
	private OWLOntology copy;
	private OWLOntology naive;	
	private OWLOntology unsupported;
	
	/**
	 *  *      A TBox axiom in normal form can be of one of four types:
	 *         
	 *         Type 1: Subclass(atom or conjunction of atoms, atom or
	 *         disjunction of atoms) 1a atom atom 1b atom disj
	 *         Type 2: Subclass(atom, exists property atom)
	 *         Type 3: Subclass(atom, forall property atom)
	 *         Type 4: Subclass(exists property atom, atom)
	 *         
	 */
	
	public NormalFormTools() {
		this.copy = Utils.newEmptyOntology();
		this.naive = Utils.newEmptyOntology();
		this.unsupported = Utils.newEmptyOntology();
	}
	
	/**
	 * Check of an OWLClassExpression is a fresh concept generated during normalisation
	 * @param expr an OWLClassExpression
	 * @return true if expr is a fresh concept
	 */
	private boolean isFresh(OWLClassExpression expr) {
		
		if (expr.toString().contains("FRESH#")) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * This function prepares input ontology to be normalised and classifies axioms that could not be normalised
	 * 
	 * @param ontology
	 */
	private void prepareOntology (OWLOntology ontology) {
		this.copy.addAxioms(ontology.axioms());
		
		Stream<OWLAxiom> tBoxAxioms = this.copy.tboxAxioms(Imports.EXCLUDED);
		tBoxAxioms.forEach((ax) -> {
			this.copy.remove(ax);

			try {
				this.copy.addAxioms(NormalizationTools.asSubClassOfAxioms(ax));
			}
			catch (Exception f) {
					System.out.println("Unsupported axioms:" + ax.toString());
					this.unsupported.addAxiom(ax);
			}
		
		});
	}
	
	/**
	 * Only axioms type 1 (A) (atom, atom) are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type1ANormalisedasKF(Metamodel kf, OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose
		
		this.prepareOntology(ontology);
		
		this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
		this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));
		
		Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());
		
		tBoxAxiomsCopy.forEach(
				(ax) -> {
					try {
						Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
						ax_n.forEach(
							(ax_sub) -> {
								if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
									OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
									OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();
									
									// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
									// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
									
									if (NormalForm.typeOneSubClassAxiom(left, right)) {											
										// atom atom
										if (NormalForm.isAtom(left) && NormalForm.isAtom(right)) {
											this.type1AasKF(kf, left, right);	
										}
									}
								}
								else {
									System.out.println("Do nothing:" + ax.toString());
								}
							});
						this.naive.addAxioms(ax_n);
					}
					catch (Exception fex) {
						System.out.println("Unsupported axioms:" + ax.toString());
						this.unsupported.addAxiom(ax);
					}
				});
	}
	
	/**
	 * Only axioms type 1 (B) (atom, disj) are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type1BNormalisedasKF(Metamodel kf, OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose
		
		this.prepareOntology(ontology);
		
		this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
		this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));
		
		Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());
		
		tBoxAxiomsCopy.forEach(
				(ax) -> {
					try {
						Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
						ax_n.forEach(
							(ax_sub) -> {
								if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
									OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
									OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();
									
									// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
									// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
									
									if (NormalForm.typeOneSubClassAxiom(left, right)) {											
										// atom disj
										if (NormalForm.isAtom(left) && NormalForm.isDisjunctionOfAtoms(right)) {
											this.type1BasKF(kf, left, right);	
										}
									}
								}
								else {
									System.out.println("Do nothing:" + ax.toString());
								}
							});
						this.naive.addAxioms(ax_n);
					}
					catch (Exception fex) {
						System.out.println("Unsupported axioms:" + ax.toString());
						this.unsupported.addAxiom(ax);
					}
				});
	}
	
	/**
	 * Only axioms type 1 (C) (conj, atom) are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type1CNormalisedasKF(Metamodel kf, OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose
		
		this.prepareOntology(ontology);
		
		this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
		this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));
		
		Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());
		
		tBoxAxiomsCopy.forEach(
				(ax) -> {
					try {
						Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
						ax_n.forEach(
							(ax_sub) -> {
								if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
									OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
									OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();
									
									// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
									// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
									
									if (NormalForm.typeOneSubClassAxiom(left, right)) {											
										// conj atom
										if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isAtom(right)) {
											this.type1CasKF(kf, left, right);	
										}
									}
								}
								else {
									System.out.println("Do nothing:" + ax.toString());
								}
							});
						this.naive.addAxioms(ax_n);
					}
					catch (Exception fex) {
						System.out.println("Unsupported axioms:" + ax.toString());
						this.unsupported.addAxiom(ax);
					}
				});
	}
	
	/**
	 * Only axioms type 1 (D) (conj, disj) are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type1DNormalisedasKF(Metamodel kf, OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose
		
		this.prepareOntology(ontology);
		
		this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
		this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));
		
		Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());
		
		tBoxAxiomsCopy.forEach(
				(ax) -> {
					try {
						Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
						ax_n.forEach(
							(ax_sub) -> {
								if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
									OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
									OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();
									
									// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
									// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
									
									if (NormalForm.typeOneSubClassAxiom(left, right)) {											
										// conj atom
										if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isDisjunctionOfAtoms(right)) {
											this.type1DasKF(kf, left, right);	
										}
									}
								}
								else {
									System.out.println("Do nothing:" + ax.toString());
								}
							});
						this.naive.addAxioms(ax_n);
					}
					catch (Exception fex) {
						System.out.println("Unsupported axioms:" + ax.toString());
						this.unsupported.addAxiom(ax);
					}
				});
	}
	
	/**
	 * All type of normalised axioms are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 * @return a normalised ontology to be imported
	 */
	public void asKF(Metamodel kf, OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

		this.prepareOntology(ontology);
		
		this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
		this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));
		
		Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());
		
		OWLReasoner reasoner = Utils.getHermitReasoner(ontology);

		tBoxAxiomsCopy.forEach(
				(ax) -> {
					try {
						Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
						
						ax_n.forEach(
								(ax_sub) -> {
									if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
															
										OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
										OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();
									
										// Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
										// A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
									
										if (NormalForm.typeOneSubClassAxiom(left, right)) {

										// atom atom
											if (NormalForm.isAtom(left) && NormalForm.isAtom(right)) {
												this.type1AasKF(kf, left, right);	
											// atom disj	
											} else if (NormalForm.isAtom(left) && NormalForm.isDisjunctionOfAtoms(right)) {
													this.type1BasKF(kf, left, right);
											// conj atom	
											} else if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isAtom(right)) {
												this.type1CasKF(kf, left, right);	
											// conj disj
											} else if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isDisjunctionOfAtoms(right)) {	
												this.type1DasKF(kf, left, right);	
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
										System.out.println("Do nothing:" + ax_sub.toString());
									}
								});
						
								this.naive.addAxioms(ax_n);
							}
							catch (Exception fex) {
								System.out.println("Unsupported axioms:" + ax.toString());
								this.unsupported.addAxiom(ax);
							}
						});
		
		//System.out.println("\n ************ Axioms Normalised \n");
		
		//naive.tboxAxioms(Imports.EXCLUDED).forEach(ax_e -> System.out.println(Utils.pretty("-- " + ax_e.toString())));
		
		//System.out.println("\n ************List Unsupported ClassExpressions and Axioms in Normalization App\n");
		// After normalize, copy again the unsupported axioms
		//unsupported.axioms().forEach(System.out::println);
		
		/*
		//naive.addAxioms(unsupported.axioms());
		
		
		OWLReasoner reasoner = Utils.getHermitReasoner(naive);
		//assert (ontology.axioms().allMatch(ax -> reasoner.isEntailed(ax)));
		
		copy.addAxioms(unsupported.axioms());
		
		// check every axiom of naive is entailed in the copy of the original ontology
		// with extended signature
		copy.addAxioms(FreshAtoms.getFreshAtomsEquivalenceAxioms());
		OWLReasoner reasonerBis = Utils.getHermitReasoner(copy);
		assert (naive.axioms().allMatch(ax -> reasonerBis.isEntailed(ax)));	
		
		naive.tboxAxioms(Imports.EXCLUDED).forEach(ax -> System.out.println(Utils.pretty("-- " + ax.toString())));
		
		return naive;*/

	}
	
	
	/**
	 * Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
	 *  - A \sqsubseteq B or  (atom, atom)
	 * 
	 * if OWLClassExpression is a FRESH concept, we generated a new IRI for such FRESH by adding a URL
	 * 
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type1AasKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		String left_iri = left.asOWLClass().toStringID();
		String right_iri = right.asOWLClass().toStringID();
		
		if (isFresh(left)) { left_iri = left_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + left.asOWLClass().toStringID(); }
		if (isFresh(right)) { right_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + right.asOWLClass().toStringID(); }	

		ObjectType ot_child = new ObjectType(left_iri);
		ObjectType ot_parent = new ObjectType(right_iri);
		
		kf.addEntity(ot_child);
		kf.addEntity(ot_parent);
		
		Subsumption sub = new Subsumption(
									getAlphaNumericString(8), 
									ot_parent, 
									ot_child);
		
		kf.addRelationship(sub);
	}
	
	/**
	 * Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
	 *  - A \sqsubseteq B \sqcup C (atom, disjunction of atoms)
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type1BasKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		String left_iri = left.asOWLClass().toStringID();
		if (isFresh(left)) { left_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + left.asOWLClass().toStringID(); }
		
		ObjectType ot_left = new ObjectType(left_iri);
		
		Set<OWLClassExpression> disjunctions = right.asDisjunctSet();
		ObjectType ot_fresh = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#" + right.toString());
		
		ArrayList<ObjectType> cc_list = new ArrayList();
		CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));
		
		for (OWLClassExpression d : disjunctions) {
			if (NormalForm.isAtom(d)) {
				String d_iri = d.asOWLClass().toStringID();
				if (isFresh(d)) { d_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + d.asOWLClass().toStringID(); }
				ObjectType ot = new ObjectType(d_iri);
				
				kf.addEntity(ot);
				cc_list.add(ot);
				
				Subsumption sub_fresh = new Subsumption(
						getAlphaNumericString(8), 
						ot_fresh, 
						ot,
						cc);
				kf.addRelationship(sub_fresh);
			}
		}
		
		cc.setEntities(cc_list);
		kf.addConstraint(cc);
		
		kf.addEntity(ot_left);
		kf.addEntity(ot_fresh);
		
		Subsumption sub = new Subsumption(
									getAlphaNumericString(8), 
									ot_fresh, 
									ot_left);
		kf.addRelationship(sub);
	}
	
	/**
	 * Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
	 *  - A \sqcap B \sqsubseteq C or (conjuction of atoms, atom)
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type1CasKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		String right_iri = right.asOWLClass().toStringID();
		if (isFresh(right)) { right_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + right.asOWLClass().toStringID(); }
		
		ObjectType ot_right = new ObjectType(right_iri);
		
		Set<OWLClassExpression> conjunctions = left.asConjunctSet();
		ObjectType ot_fresh = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#" + left.toString());
		
		for (OWLClassExpression c : conjunctions) {
			if (NormalForm.isAtom(c)) {
				String c_iri = c.asOWLClass().toStringID();
				if (isFresh(c)) { c_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + c.asOWLClass().toStringID(); }
				ObjectType ot = new ObjectType(c_iri);
				
				kf.addEntity(ot);
				
				Subsumption sub_fresh = new Subsumption(
						getAlphaNumericString(8), 
						ot, 
						ot_fresh);
				kf.addRelationship(sub_fresh);
			}
		}
		
		kf.addEntity(ot_right);
		kf.addEntity(ot_fresh);
		
		Subsumption sub = new Subsumption(
									getAlphaNumericString(8), 
									ot_right,
									ot_fresh);
		kf.addRelationship(sub);
	}
	
	/**
	 * Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
	 *  - A \sqcap B \sqsubseteq C \sqcup D (conjunction of atoms, disjunction of atoms)
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type1DasKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		Set<OWLClassExpression> disjunctions = right.asDisjunctSet();
		ObjectType ot_fresh_d = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#" + right.toString());
		
		ArrayList<ObjectType> cc_list = new ArrayList();
		CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));
		
		for (OWLClassExpression d : disjunctions) {
			if (NormalForm.isAtom(d)) {
				String d_iri = d.asOWLClass().toStringID();
				if (isFresh(d)) { d_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + d.asOWLClass().toStringID(); }
				ObjectType ot = new ObjectType(d_iri);
				
				kf.addEntity(ot);
				cc_list.add(ot);
				
				Subsumption sub_fresh_d = new Subsumption(
						getAlphaNumericString(8), 
						ot_fresh_d, 
						ot,
						cc);
				
				kf.addRelationship(sub_fresh_d);
			}
		}
		
		cc.setEntities(cc_list);
		kf.addConstraint(cc);
		kf.addEntity(ot_fresh_d);
		
		Set<OWLClassExpression> conjunctions = left.asConjunctSet();
		ObjectType ot_fresh_c = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#" + left.toString());
		
		for (OWLClassExpression c : conjunctions) {
			if (NormalForm.isAtom(c)) {
				String c_iri = c.asOWLClass().toStringID();
				if (isFresh(c)) { c_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + c.asOWLClass().toStringID(); }
				ObjectType ot = new ObjectType(c_iri);
				
				kf.addEntity(ot);
				
				Subsumption sub_fresh_c = new Subsumption(
						getAlphaNumericString(8), 
						ot, 
						ot_fresh_c);
				
				kf.addRelationship(sub_fresh_c);
			}
		}
		
		kf.addEntity(ot_fresh_c);
		
		Subsumption sub = new Subsumption(
									getAlphaNumericString(8), 
									ot_fresh_d,
									ot_fresh_c);
		kf.addRelationship(sub);
	}
	
}
