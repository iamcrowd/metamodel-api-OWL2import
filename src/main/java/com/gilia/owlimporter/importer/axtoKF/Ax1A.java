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
public class Ax1A extends AxToKFTools{
	
	public Ax1A() {
		super();
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
		
		if (isFresh(left)) { left_iri = left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID(); }
		if (isFresh(right)) { right_iri = URI_NORMAL_CONCEPT + right.asOWLClass().toStringID(); }	

		ObjectType ot_child = new ObjectType(left_iri);
		ObjectType ot_parent = new ObjectType(right_iri);
		
		kf.addEntity(ot_child);
		kf.addEntity(ot_parent);
		
		if (kf.getRelationship("Subsumption(" + ot_parent.getName() + "," + ot_child.getName() + ")").isNameless()) {
			Subsumption sub = new Subsumption(
					"Subsumption(" + ot_parent.getName() + "," + ot_child.getName() + ")", 
					ot_parent, 
					ot_child);

			kf.addRelationship(sub);
		}
	}

}