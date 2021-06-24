package com.gilia.owlimporter.importer;

import com.gilia.owlimporter.importer.AxToKFTools;
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

import uk.ac.manchester.cs.owl.owlapi.OWLCardinalityRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;


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

import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;

/**
 * This class implements the model based reconstructions of Normalised Axioms 
 * 
 * @see NormalForm from ontologyutils dependency
 * 
 * @author gbraun
 *
 */
public class Ax1C extends AxToKFTools{
	
	public Ax1C() {
		super();
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
		if (isFresh(right)) { right_iri = URI_NORMAL_CONCEPT + right.asOWLClass().toStringID(); }
		
		ObjectType ot_right = new ObjectType(right_iri);
		
		Set<OWLClassExpression> conjunctions = left.asConjunctSet();
		String c_iris = "";
		for (OWLClassExpression c : conjunctions) {
			if (NormalForm.isAtom(c)) {
				String c_iri = c.asOWLClass().toStringID();
				if (isFresh(c)) { 
					c_iri = URI_NORMAL_CONCEPT + c.asOWLClass().toStringID();
				}
				c_iris += c_iri + "$";
			}
		}
		ObjectType ot_fresh = new ObjectType(URI_IMPORT_CONCEPT + "INTERSECTION%" + c_iris);
        
		for (OWLClassExpression c : conjunctions) {
			if (NormalForm.isAtom(c)) {
				String c_iri = c.asOWLClass().toStringID();
				if (isFresh(c)) { 
					c_iri = URI_NORMAL_CONCEPT + c.asOWLClass().toStringID();
				}
				ObjectType ot = new ObjectType(c_iri);

				kf.addEntity(ot);
				
				if (kf.getRelationship("Subsumption(" + ot.getName() + "," + ot_fresh.getName() + ")").isNameless()) {
					Subsumption sub_fresh = new Subsumption(
							"Subsumption(" + ot.getName() + "," + ot_fresh.getName() + ")", 
							ot, 
							ot_fresh);
					kf.addRelationship(sub_fresh);
				}
			}
		}
		
		kf.addEntity(ot_right);
		kf.addEntity(ot_fresh);
		
		if (kf.getRelationship("Subsumption(" + ot_right.getName() + "," + ot_fresh.getName() + ")").isNameless()) {
			Subsumption sub = new Subsumption(
					"Subsumption(" + ot_right.getName() + "," + ot_fresh.getName() + ")", 
					ot_right,
					ot_fresh);
			kf.addRelationship(sub);
		}
	}

}