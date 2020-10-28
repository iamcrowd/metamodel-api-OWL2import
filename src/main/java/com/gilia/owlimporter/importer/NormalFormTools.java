package com.gilia.owlimporter.importer;

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
import java.util.List;
//import com.sun.tools.javac.util.List;
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
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.role.Role;

import static com.gilia.utils.Utils.getAlphaNumericString;
import com.google.common.base.CaseFormat;
//import com.sun.tools.javac.util.List;
import com.gilia.owlimporter.importer.classExpression.Class;

import uk.ac.manchester.cs.owl.owlapi.OWLCardinalityRestrictionImpl;
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

import static com.gilia.utils.Constants.TYPE2_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_EXACT_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_EXACT_CARD_AXIOM;

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
	 * Only axioms type 2 are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type2NormalisedasKF(Metamodel kf, OWLOntology ontology) {
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
									
									if (NormalForm.typeTwoSubClassAxiom(left, right)) {	//Object
										this.type2asKF(kf, left, right, TYPE2_SUBCLASS_AXIOM);	
									} 
									else if (NormalForm.typeTwoMinCardAxiom(left, right)) {
										this.type2asKF(kf, left, right, TYPE2_MIN_CARD_AXIOM);	
									}
									else if (NormalForm.typeTwoMaxCardAxiom(left, right)) {
										this.type2asKF(kf, left, right, TYPE2_MAX_CARD_AXIOM);	
									}
									else if (NormalForm.typeTwoExactCardAxiom(left, right)) {
										this.type2asKF(kf, left, right, TYPE2_EXACT_CARD_AXIOM);	
									}
									else if (NormalForm.typeTwoDataSubClassAxiom(left, right)) { //Data
										System.out.println("typeTwoDataSubClassAxiom");
										//this.type2asKF(kf, left, right, TYPE2_DATA_SUBCLASS_AXIOM);	
									}
									else if (NormalForm.typeTwoDataMinCardAxiom(left, right)) {
										System.out.println("typeTwoDataMinCardAxiom");
										//this.type2asKF(kf, left, right, TYPE2_DATA_MIN_CARD_AXIOM);	
									}
									else if (NormalForm.typeTwoDataMaxCardAxiom(left, right)) {
										System.out.println("typeTwoDataMaxCardAxiom");
										//this.type2asKF(kf, left, right, TYPE2_DATA_MAX_CARD_AXIOM);	
									}
									else if (NormalForm.typeTwoDataExactCardAxiom(left, right)) {
										System.out.println("typeTwoDataExactCardAxiom");
										//this.type2asKF(kf, left, right, TYPE2_DATA_EXACT_CARD_AXIOM);	
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
	 * Only axioms type 3 are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type3NormalisedasKF(Metamodel kf, OWLOntology ontology) {
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
									
									// Subclass(atom, forall property filler)
									
									if (NormalForm.typeThreeSubClassAxiom(left, right)) {											
										this.type3asKF(kf, left, right);	
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
	 * Only axioms type 4 are imported
	 * 
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 */
	public void type4NormalisedasKF(Metamodel kf, OWLOntology ontology) {
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
									
									if (NormalForm.typeFourSubClassAxiom(left, right)) {											
										this.type4asKF(kf, left, right);	
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
									
										if (NormalForm.typeTwoSubClassAxiom(left, right)) {	//Object		 								
											this.type2asKF(kf, left, right, TYPE2_SUBCLASS_AXIOM);	
										} 
										else if (NormalForm.typeTwoMinCardAxiom(left, right)) {
											this.type2asKF(kf, left, right, TYPE2_MIN_CARD_AXIOM);	
										}
										else if (NormalForm.typeTwoMaxCardAxiom(left, right)) {
											this.type2asKF(kf, left, right, TYPE2_MAX_CARD_AXIOM);	
										}
										else if (NormalForm.typeTwoExactCardAxiom(left, right)) {
											this.type2asKF(kf, left, right, TYPE2_EXACT_CARD_AXIOM);	
										}
										else if (NormalForm.typeTwoDataSubClassAxiom(left, right)) { //Data
											//this.type2asKF(kf, left, right, TYPE2_DATA_SUBCLASS_AXIOM);	
										}
										else if (NormalForm.typeTwoDataMinCardAxiom(left, right)) {
											//this.type2asKF(kf, left, right, TYPE2_DATA_MIN_CARD_AXIOM);	
										}
										else if (NormalForm.typeTwoDataMaxCardAxiom(left, right)) {
											//this.type2asKF(kf, left, right, TYPE2_DATA_MAX_CARD_AXIOM);	
										}
										else if (NormalForm.typeTwoDataExactCardAxiom(left, right)) {
											//this.type2asKF(kf, left, right, TYPE2_DATA_EXACT_CARD_AXIOM);	
										}
										
										if (NormalForm.typeThreeSubClassAxiom(left, right)) {
											this.type3asKF(kf, left, right);
										}
										if (NormalForm.typeFourSubClassAxiom(left, right)) {
											this.type4asKF(kf, left, right);
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
		ObjectType ot_fresh = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PatternAorB");
		
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
		ObjectType ot_fresh = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PatternAandB");
		
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
		ObjectType ot_fresh_d = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PatternAorB");
		
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
		ObjectType ot_fresh_c = new ObjectType("http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PatternAandB");
		
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
	
	/**
	 * Subclass(atom, exists property atom)
	 * 
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type2asKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right, String TYPE) {
		
		String left_iri = left.asOWLClass().toStringID();
		if (isFresh(left)) { left_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + left.asOWLClass().toStringID(); }
		
		OWLClassExpression filler = null;
		OWLPropertyExpression property = null;
		String prop_iri = "";
		
		if (TYPE == TYPE2_SUBCLASS_AXIOM) {
			filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
			property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty();
			prop_iri = property.asOWLObjectProperty().toStringID();
			
		} else if ((TYPE == TYPE2_MIN_CARD_AXIOM) ||
				   (TYPE == TYPE2_MAX_CARD_AXIOM) ||
				   (TYPE == TYPE2_EXACT_CARD_AXIOM)) {
			filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getFiller();
			property = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getProperty();
			prop_iri = property.asOWLObjectProperty().toStringID();
			
		}
	
		if (NormalForm.isAtom(filler)) {
			
			String filler_iri = filler.asOWLClass().toStringID();
			if (isFresh(filler)) { filler_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + filler.asOWLClass().toStringID(); }
			
			//add subsumptions
			String fresh_O = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#O";
			ObjectType ot_fresh_O = new ObjectType(fresh_O);
			
			ObjectType ot_left = new ObjectType(left_iri);
			ObjectType ot_filler = new ObjectType(filler_iri);
			
			Subsumption sub_fresh_leftORfiller = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_left);
			
			Subsumption sub_fresh_leftORfiller_2 = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_filler);
			
			kf.addEntity(ot_fresh_O);
			kf.addEntity(ot_left);
			kf.addEntity(ot_filler);
			kf.addRelationship(sub_fresh_leftORfiller);
			kf.addRelationship(sub_fresh_leftORfiller_2);
			
			String fresh_C_PAB = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CPAB";
			String fresh_C_P = prop_iri; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CP";
			
			ObjectType ot_fresh_C_PAB = new ObjectType(fresh_C_PAB);
			ObjectType ot_C_P = new ObjectType(fresh_C_P);
			
			Subsumption sub_fresh_CP_CPAB = new Subsumption(
					getAlphaNumericString(8), 
					ot_C_P, 
					ot_fresh_C_PAB);
			
			kf.addEntity(ot_fresh_C_PAB);
			kf.addEntity(ot_C_P);
			kf.addRelationship(sub_fresh_CP_CPAB);
			
			//add fresh relationships
			
			String rel_fresh_PAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB1";
			
			String role_fresh_CPAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB1";
			String role_fresh_APAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleAPAB1";
			
			ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			
			Integer cardinality;
			String min = "1";
			String max = "*";
			
			switch (TYPE) {
				case TYPE2_SUBCLASS_AXIOM:
					min = "1";
					max = "*";
				
				break;
				case TYPE2_MIN_CARD_AXIOM:
					cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getCardinality();
					if (cardinality == 1) {
						min = "1";
						max = "*";
					}
					else if (cardinality > 1) {
						min = cardinality.toString();
						max = "*";
					}
					
				
				break;
				case TYPE2_MAX_CARD_AXIOM:
					cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getCardinality();
					min = "0";
					max = cardinality.toString();
					
				break;
				case TYPE2_EXACT_CARD_AXIOM:
					cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getCardinality();
					min = cardinality.toString();
					max = cardinality.toString();
				
				break;
				case TYPE2_DATA_SUBCLASS_AXIOM:
				
				break;
				case TYPE2_DATA_MIN_CARD_AXIOM:
				
				break;
				case TYPE2_DATA_MAX_CARD_AXIOM:
				
				break;
				case TYPE2_DATA_EXACT_CARD_AXIOM:
				
				break;
				
			default:
				min = "1";
				max = "*";
				break;
			}
			
			ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), min, max);
			
			kf.addConstraint(otc_RoleCPAB1);
			kf.addConstraint(otc_RoleAPAB1);
			
			ArrayList<Entity> e1 = new ArrayList();
			e1.add(ot_fresh_C_PAB);
			e1.add(ot_left);
			
			Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1); 
			
			Role role_fresh_CPAB1 = new Role(role_fresh_CPAB1_iri, ot_fresh_C_PAB, r_fresh_PAB1, otc_RoleCPAB1); 
			Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_left, r_fresh_PAB1, otc_RoleAPAB1); 
			
			kf.addRole(role_fresh_CPAB1);
			kf.addRole(role_fresh_APAB1);
			
			ArrayList<Role> r1 = new ArrayList();
			r1.add(role_fresh_CPAB1);
			r1.add(role_fresh_APAB1);
			
			r_fresh_PAB1.setRoles(r1); 
			
			String rel_fresh_PAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB2";
			
			String role_fresh_CPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB2";
			String role_fresh_BPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleBPAB2";
			
			ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAB2);
			kf.addConstraint(otc_RoleBPAB2);
			
			ArrayList<Entity> e2 = new ArrayList();
			e2.add(ot_fresh_C_PAB);
			e2.add(ot_filler);
			
			Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e2);
			
			Role role_fresh_CPAB2 = new Role(role_fresh_CPAB2_iri, ot_fresh_C_PAB, r_fresh_PAB2, otc_RoleCPAB2); 
			Role role_fresh_BPAB2 = new Role(role_fresh_BPAB2_iri, ot_filler, r_fresh_PAB2, otc_RoleBPAB2);
			
			kf.addRole(role_fresh_CPAB2);
			kf.addRole(role_fresh_BPAB2);
			
			ArrayList<Role> r2 = new ArrayList();
			r2.add(role_fresh_CPAB2);
			r2.add(role_fresh_BPAB2);
			
			r_fresh_PAB2.setRoles(r2);
			
			kf.addRelationship(r_fresh_PAB1);
			kf.addRelationship(r_fresh_PAB2);
			
			//add original relationships
			String rel_P1_iri = prop_iri + "1"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P1";
			
			String role_fresh_CPP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP1";
			String role_fresh_OCP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP1";
			
			ObjectTypeCardinality otc_RoleCPP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP1);
			kf.addConstraint(otc_RoleOCP1);
			
			ArrayList<Entity> e3 = new ArrayList();
			e3.add(ot_C_P);
			e3.add(ot_fresh_O);
			
			Relationship r_P1 = new Relationship(rel_P1_iri, e3); 
			
			Role role_fresh_CPP1 = new Role(role_fresh_CPP1_iri, ot_C_P, r_P1, otc_RoleCPP1); 
			Role role_fresh_OCP1 = new Role(role_fresh_OCP1_iri, ot_fresh_O, r_P1, otc_RoleOCP1); 
			
			kf.addRole(role_fresh_CPP1);
			kf.addRole(role_fresh_OCP1);
			
			ArrayList<Role> r3 = new ArrayList();
			r3.add(role_fresh_CPP1);
			r3.add(role_fresh_OCP1);
			
			r_P1.setRoles(r3); 
			
			String rel_P2_iri = prop_iri + "2"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P2";
			
			String role_fresh_CPP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP2";
			String role_fresh_OCP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP2";
			
			ObjectTypeCardinality otc_RoleCPP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP2);
			kf.addConstraint(otc_RoleOCP2);
			
			Relationship r_P2 = new Relationship(rel_P2_iri, e3); 
			
			Role role_fresh_CPP2 = new Role(role_fresh_CPP2_iri, ot_C_P, r_P2, otc_RoleCPP2); 
			Role role_fresh_OCP2 = new Role(role_fresh_OCP2_iri, ot_fresh_O, r_P2, otc_RoleOCP2); 
			
			kf.addRole(role_fresh_CPP2);
			kf.addRole(role_fresh_OCP2);
			
			ArrayList<Role> r4 = new ArrayList();
			r4.add(role_fresh_CPP2);
			r4.add(role_fresh_OCP2);
			
			r_P2.setRoles(r4);
			
			kf.addRelationship(r_P1);
			kf.addRelationship(r_P2);
		
		}
	}
	
	/**
	 * Subclass(atom, forall property atom)
	 * 
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type3asKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		String left_iri = left.asOWLClass().toStringID();
		if (isFresh(left)) { left_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + left.asOWLClass().toStringID(); }
		
		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
		OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty();
	
		String prop_iri = property.asOWLObjectProperty().toStringID();		
		
		ArrayList<ObjectType> dot_list = new ArrayList();
		DisjointObjectType dot = new DisjointObjectType(getAlphaNumericString(8));
		
		if (NormalForm.isAtom(filler)) {
			String filler_iri = filler.asOWLClass().toStringID();
			if (isFresh(filler)) { filler_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + filler.asOWLClass().toStringID(); }
			
			//add subsumptions
			String fresh_O = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#O";
			ObjectType ot_fresh_O = new ObjectType(fresh_O);
			
			ObjectType ot_left = new ObjectType(left_iri);
			ObjectType ot_complement_left = new ObjectType(left_iri + "c");
			
			ObjectType ot_filler = new ObjectType(filler_iri);
			
			dot_list.add(ot_left);
			dot_list.add(ot_complement_left);
			
			dot.setEntities(dot_list);
			
			Subsumption sub_fresh_leftORcomplement = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_left,
					dot);
			
			Subsumption sub_fresh_leftORcomplement_2 = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_complement_left,
					dot);
			
			kf.addEntity(ot_fresh_O);
			kf.addEntity(ot_left);
			kf.addEntity(ot_complement_left);
			kf.addEntity(ot_filler);
			kf.addConstraint(dot);
			
			kf.addRelationship(sub_fresh_leftORcomplement);
			kf.addRelationship(sub_fresh_leftORcomplement_2);
			
			//add subsumption CP
			String fresh_C_PAB = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CPAB";
			String fresh_C_PABc = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CPABc";
			String fresh_C_P = prop_iri; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CP";
			
			ObjectType ot_fresh_C_PAB = new ObjectType(fresh_C_PAB);
			ObjectType ot_fresh_C_PABc = new ObjectType(fresh_C_PABc);
			ObjectType ot_C_P = new ObjectType(fresh_C_P);
			
			ArrayList<ObjectType> cc_list = new ArrayList();
			CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));
			
			cc_list.add(ot_fresh_C_PAB);
			cc_list.add(ot_fresh_C_PABc);
			
			cc.setEntities(cc_list);
			
			Subsumption sub_fresh_CP_CPAB = new Subsumption(
					getAlphaNumericString(8), 
					ot_C_P, 
					ot_fresh_C_PAB,
					cc);
			
			Subsumption sub_fresh_CP_CPABc = new Subsumption(
					getAlphaNumericString(8), 
					ot_C_P, 
					ot_fresh_C_PABc,
					cc);
			
			kf.addConstraint(cc);
			kf.addEntity(ot_fresh_C_PAB);
			kf.addEntity(ot_fresh_C_PABc);
			kf.addEntity(ot_C_P);
			kf.addRelationship(sub_fresh_CP_CPAB);
			kf.addRelationship(sub_fresh_CP_CPABc);
			
			//add fresh relationships
			
			String rel_fresh_PAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB1";
			
			String role_fresh_CPAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB1";
			String role_fresh_APAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleAPAB1";
			
			ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAB1);
			kf.addConstraint(otc_RoleAPAB1);
			
			ArrayList<Entity> e1 = new ArrayList();
			e1.add(ot_fresh_C_PAB);
			e1.add(ot_left);
			
			Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1); 
			
			Role role_fresh_CPAB1 = new Role(role_fresh_CPAB1_iri, ot_fresh_C_PAB, r_fresh_PAB1, otc_RoleCPAB1); 
			Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_left, r_fresh_PAB1, otc_RoleAPAB1); 
			
			kf.addRole(role_fresh_CPAB1);
			kf.addRole(role_fresh_APAB1);
			
			ArrayList<Role> r1 = new ArrayList();
			r1.add(role_fresh_CPAB1);
			r1.add(role_fresh_APAB1);
			
			r_fresh_PAB1.setRoles(r1);
			
			//
			
			String rel_fresh_PAcB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAcB1";
			
			String role_fresh_CPAcB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAcB1";
			String role_fresh_AcPAcB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleAcPAcB1";
			
			ObjectTypeCardinality otc_RoleCPAcB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleAcPAcB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAcB1);
			kf.addConstraint(otc_RoleAcPAcB1);
			
			ArrayList<Entity> e2 = new ArrayList();
			e2.add(ot_fresh_C_PABc);
			e2.add(ot_complement_left);
			
			Relationship r_fresh_PAcB1 = new Relationship(rel_fresh_PAcB1_iri, e2); 
			
			Role role_fresh_CPAcB1 = new Role(role_fresh_CPAcB1_iri, ot_fresh_C_PABc, r_fresh_PAcB1, otc_RoleCPAcB1); 
			Role role_fresh_AcPAcB1 = new Role(role_fresh_AcPAcB1_iri, ot_complement_left, r_fresh_PAcB1, otc_RoleAcPAcB1); 
			
			kf.addRole(role_fresh_CPAcB1);
			kf.addRole(role_fresh_AcPAcB1);
			
			ArrayList<Role> r2 = new ArrayList();
			r2.add(role_fresh_CPAcB1);
			r2.add(role_fresh_AcPAcB1);
			
			r_fresh_PAcB1.setRoles(r2);
			
			//
			
			String rel_fresh_PAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB2";
			
			String role_fresh_CPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB2";
			String role_fresh_BPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleBPAB2";
			
			ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAB2);
			kf.addConstraint(otc_RoleBPAB2);
			
			ArrayList<Entity> e3 = new ArrayList();
			e3.add(ot_fresh_C_PAB);
			e3.add(ot_filler);
			
			Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e3);
			
			Role role_fresh_CPAB2 = new Role(role_fresh_CPAB2_iri, ot_fresh_C_PAB, r_fresh_PAB2, otc_RoleCPAB2); 
			Role role_fresh_BPAB2 = new Role(role_fresh_BPAB2_iri, ot_filler, r_fresh_PAB2, otc_RoleBPAB2);
			
			kf.addRole(role_fresh_CPAB2);
			kf.addRole(role_fresh_BPAB2);
			
			ArrayList<Role> r3 = new ArrayList();
			r3.add(role_fresh_CPAB2);
			r3.add(role_fresh_BPAB2);
			
			r_fresh_PAB2.setRoles(r3);
			
			kf.addRelationship(r_fresh_PAB1);
			kf.addRelationship(r_fresh_PAB2);
			
			//add original relationships
			String rel_P1_iri = prop_iri + "1"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P1";
			
			String role_fresh_CPP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP1";
			String role_fresh_OCP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP1";
			
			ObjectTypeCardinality otc_RoleCPP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP1);
			kf.addConstraint(otc_RoleOCP1);
			
			ArrayList<Entity> e4 = new ArrayList();
			e4.add(ot_C_P);
			e4.add(ot_fresh_O);
			
			Relationship r_P1 = new Relationship(rel_P1_iri, e4); 
			
			Role role_fresh_CPP1 = new Role(role_fresh_CPP1_iri, ot_C_P, r_P1, otc_RoleCPP1); 
			Role role_fresh_OCP1 = new Role(role_fresh_OCP1_iri, ot_fresh_O, r_P1, otc_RoleOCP1); 
			
			kf.addRole(role_fresh_CPP1);
			kf.addRole(role_fresh_OCP1);
			
			ArrayList<Role> r4 = new ArrayList();
			r4.add(role_fresh_CPP1);
			r4.add(role_fresh_OCP1);
			
			r_P1.setRoles(r4); 
			
			String rel_P2_iri = prop_iri + "2"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P2";
			
			String role_fresh_CPP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP2";
			String role_fresh_OCP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP2";
			
			ObjectTypeCardinality otc_RoleCPP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP2);
			kf.addConstraint(otc_RoleOCP2);
			
			Relationship r_P2 = new Relationship(rel_P2_iri, e4); 
			
			Role role_fresh_CPP2 = new Role(role_fresh_CPP2_iri, ot_C_P, r_P2, otc_RoleCPP2); 
			Role role_fresh_OCP2 = new Role(role_fresh_OCP2_iri, ot_fresh_O, r_P2, otc_RoleOCP2); 
			
			kf.addRole(role_fresh_CPP2);
			kf.addRole(role_fresh_OCP2);
			
			ArrayList<Role> r5 = new ArrayList();
			r5.add(role_fresh_CPP2);
			r5.add(role_fresh_OCP2);
			
			r_P2.setRoles(r5);
			
			kf.addRelationship(r_P1);
			kf.addRelationship(r_P2);
			
			// add roles subsumptions
		
		}
	}
	
	/**
	 * Subclass(exists property atom, atom)
	 * 
	 * @param kf
	 * @param left
	 * @param right
	 */
	public void type4asKF (Metamodel kf, OWLClassExpression left, OWLClassExpression right) {
		
		String right_iri = right.asOWLClass().toStringID();
		if (isFresh(right)) { right_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + right.asOWLClass().toStringID(); }
		
		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) left).getFiller();
		OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) left).getProperty();
	
		String prop_iri = property.asOWLObjectProperty().toStringID();		
		
		if (NormalForm.isAtom(filler)) {
			String filler_iri = filler.asOWLClass().toStringID();
			if (isFresh(filler)) { filler_iri = "http://crowd.fi.uncoma.edu.ar/NORMAL" + filler.asOWLClass().toStringID(); }
			
			//add subsumptions
			String fresh_O = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#O";
			ObjectType ot_fresh_O = new ObjectType(fresh_O);
			
			ObjectType ot_right = new ObjectType(right_iri);
			ObjectType ot_filler = new ObjectType(filler_iri);
			
			Subsumption sub_fresh_rightORfiller = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_right);
			
			Subsumption sub_fresh_rightORfiller_2 = new Subsumption(
					getAlphaNumericString(8), 
					ot_fresh_O, 
					ot_filler);
			
			kf.addEntity(ot_fresh_O);
			kf.addEntity(ot_right);
			kf.addEntity(ot_filler);
			kf.addRelationship(sub_fresh_rightORfiller);
			kf.addRelationship(sub_fresh_rightORfiller_2);
			
			String fresh_C_PAB = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CPAB";
			String fresh_C_P = prop_iri; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CP";
			
			ObjectType ot_fresh_C_PAB = new ObjectType(fresh_C_PAB);
			ObjectType ot_C_P = new ObjectType(fresh_C_P);
			
			Subsumption sub_fresh_CP_CPAB = new Subsumption(
					getAlphaNumericString(8), 
					ot_C_P, 
					ot_fresh_C_PAB);
			
			kf.addEntity(ot_fresh_C_PAB);
			kf.addEntity(ot_C_P);
			kf.addRelationship(sub_fresh_CP_CPAB);
			
			//add fresh relationships
			
			String rel_fresh_PAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB1";
			
			String role_fresh_CPAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB1";
			String role_fresh_APAB1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleAPAB1";
			
			ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAB1);
			kf.addConstraint(otc_RoleAPAB1);
			
			ArrayList<Entity> e1 = new ArrayList();
			e1.add(ot_fresh_C_PAB);
			e1.add(ot_right);
			
			Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1); 
			
			Role role_fresh_CPAB1 = new Role(role_fresh_CPAB1_iri, ot_fresh_C_PAB, r_fresh_PAB1, otc_RoleCPAB1); 
			Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_right, r_fresh_PAB1, otc_RoleAPAB1); 
			
			kf.addRole(role_fresh_CPAB1);
			kf.addRole(role_fresh_APAB1);
			
			ArrayList<Role> r1 = new ArrayList();
			r1.add(role_fresh_CPAB1);
			r1.add(role_fresh_APAB1);
			
			r_fresh_PAB1.setRoles(r1); 
			
			String rel_fresh_PAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#PAB2";
			
			String role_fresh_CPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPAB2";
			String role_fresh_BPAB2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleBPAB2";
			
			ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");
			
			kf.addConstraint(otc_RoleCPAB2);
			kf.addConstraint(otc_RoleBPAB2);
			
			ArrayList<Entity> e2 = new ArrayList();
			e2.add(ot_fresh_C_PAB);
			e2.add(ot_filler);
			
			Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e2);
			
			Role role_fresh_CPAB2 = new Role(role_fresh_CPAB2_iri, ot_fresh_C_PAB, r_fresh_PAB2, otc_RoleCPAB2); 
			Role role_fresh_BPAB2 = new Role(role_fresh_BPAB2_iri, ot_filler, r_fresh_PAB2, otc_RoleBPAB2);
			
			kf.addRole(role_fresh_CPAB2);
			kf.addRole(role_fresh_BPAB2);
			
			ArrayList<Role> r2 = new ArrayList();
			r2.add(role_fresh_CPAB2);
			r2.add(role_fresh_BPAB2);
			
			r_fresh_PAB2.setRoles(r2);
			
			kf.addRelationship(r_fresh_PAB1);
			kf.addRelationship(r_fresh_PAB2);
			
			//add original relationships
			String rel_P1_iri = prop_iri + 1; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P1";
			
			String role_fresh_CPP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP1";
			String role_fresh_OCP1_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP1";
			
			ObjectTypeCardinality otc_RoleCPP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP1);
			kf.addConstraint(otc_RoleOCP1);
			
			ArrayList<Entity> e3 = new ArrayList();
			e3.add(ot_C_P);
			e3.add(ot_fresh_O);
			
			Relationship r_P1 = new Relationship(rel_P1_iri, e3); 
			
			Role role_fresh_CPP1 = new Role(role_fresh_CPP1_iri, ot_C_P, r_P1, otc_RoleCPP1); 
			Role role_fresh_OCP1 = new Role(role_fresh_OCP1_iri, ot_fresh_O, r_P1, otc_RoleOCP1); 
			
			kf.addRole(role_fresh_CPP1);
			kf.addRole(role_fresh_OCP1);
			
			ArrayList<Role> r3 = new ArrayList();
			r3.add(role_fresh_CPP1);
			r3.add(role_fresh_OCP1);
			
			r_P1.setRoles(r3); 
			
			String rel_P2_iri = prop_iri + 2; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P2";
			
			String role_fresh_CPP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleCPP2";
			String role_fresh_OCP2_iri = "http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#RoleOCP2";
			
			ObjectTypeCardinality otc_RoleCPP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP2);
			kf.addConstraint(otc_RoleOCP2);
			
			Relationship r_P2 = new Relationship(rel_P2_iri, e3); 
			
			Role role_fresh_CPP2 = new Role(role_fresh_CPP2_iri, ot_C_P, r_P2, otc_RoleCPP2); 
			Role role_fresh_OCP2 = new Role(role_fresh_OCP2_iri, ot_fresh_O, r_P2, otc_RoleOCP2); 
			
			kf.addRole(role_fresh_CPP2);
			kf.addRole(role_fresh_OCP2);
			
			ArrayList<Role> r4 = new ArrayList();
			r4.add(role_fresh_CPP2);
			r4.add(role_fresh_OCP2);
			
			r_P2.setRoles(r4);
			
			kf.addRelationship(r_P1);
			kf.addRelationship(r_P2);
		
		}
	}
	
}
