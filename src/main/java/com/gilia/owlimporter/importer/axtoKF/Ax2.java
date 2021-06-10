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
public class Ax2 extends AxToKFTools{
	
	public Ax2() {
		super();
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
		if (isFresh(left)) { left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID(); }
		
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
			if (isFresh(filler)) { filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID(); }
			
			//add subsumptions
			String fresh_O = URI_IMPORT_CONCEPT + "#O";
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
			
			String fresh_C_PAB = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#CPAB";
			String fresh_C_P = prop_iri;
			
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
			
			String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB1";
			
			String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB1";
			String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RAPAB1";
			
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
			
            if (role_fresh_CPAB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPAB1.getMandatoryConstraint());
            }
            
			Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_left, r_fresh_PAB1, otc_RoleAPAB1);
			
            if (role_fresh_APAB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_APAB1.getMandatoryConstraint());
            }
			
			kf.addRole(role_fresh_CPAB1);
			kf.addRole(role_fresh_APAB1);
			
			ArrayList<Role> r1 = new ArrayList();
			r1.add(role_fresh_CPAB1);
			r1.add(role_fresh_APAB1);
			
			r_fresh_PAB1.setRoles(r1); 
			
			String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB2";
			
			String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB2";
			String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RBPAB2";
			
			ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPAB2);
			kf.addConstraint(otc_RoleBPAB2);
			
			ArrayList<Entity> e2 = new ArrayList();
			e2.add(ot_fresh_C_PAB);
			e2.add(ot_filler);
			
			Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e2);
			
			Role role_fresh_CPAB2 = new Role(role_fresh_CPAB2_iri, ot_fresh_C_PAB, r_fresh_PAB2, otc_RoleCPAB2);
			
            if (role_fresh_CPAB2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPAB2.getMandatoryConstraint());
            }
			
			Role role_fresh_BPAB2 = new Role(role_fresh_BPAB2_iri, ot_filler, r_fresh_PAB2, otc_RoleBPAB2);
			
            if (role_fresh_BPAB2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_BPAB2.getMandatoryConstraint());
            }
			
			kf.addRole(role_fresh_CPAB2);
			kf.addRole(role_fresh_BPAB2);
			
			ArrayList<Role> r2 = new ArrayList();
			r2.add(role_fresh_CPAB2);
			r2.add(role_fresh_BPAB2);
			
			r_fresh_PAB2.setRoles(r2);
			
			kf.addRelationship(r_fresh_PAB1);
			kf.addRelationship(r_fresh_PAB2);
			
			//add original relationships
			String rel_P1_iri = prop_iri + "_" + getAlphaNumericString(8); 
			
			String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP1";
			String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP1";
			
			ObjectTypeCardinality otc_RoleCPP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP1);
			kf.addConstraint(otc_RoleOCP1);
			
			ArrayList<Entity> e3 = new ArrayList();
			e3.add(ot_C_P);
			e3.add(ot_fresh_O);
			
			Relationship r_P1 = new Relationship(rel_P1_iri, e3); 
			
			Role role_fresh_CPP1 = new Role(role_fresh_CPP1_iri, ot_C_P, r_P1, otc_RoleCPP1); 
			
            if (role_fresh_CPP1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPP1.getMandatoryConstraint());
            }
            
			Role role_fresh_OCP1 = new Role(role_fresh_OCP1_iri, ot_fresh_O, r_P1, otc_RoleOCP1); 
			
            if (role_fresh_OCP1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_OCP1.getMandatoryConstraint());
            }
			
			kf.addRole(role_fresh_CPP1);
			kf.addRole(role_fresh_OCP1);
			
			ArrayList<Role> r3 = new ArrayList();
			r3.add(role_fresh_CPP1);
			r3.add(role_fresh_OCP1);
			
			r_P1.setRoles(r3); 
			
			String rel_P2_iri = prop_iri + "_" + getAlphaNumericString(8);
			
			String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP2";
			String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP2";
			
			ObjectTypeCardinality otc_RoleCPP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
			ObjectTypeCardinality otc_RoleOCP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
			
			kf.addConstraint(otc_RoleCPP2);
			kf.addConstraint(otc_RoleOCP2);
			
			Relationship r_P2 = new Relationship(rel_P2_iri, e3); 
			
			Role role_fresh_CPP2 = new Role(role_fresh_CPP2_iri, ot_C_P, r_P2, otc_RoleCPP2);
			
            if (role_fresh_CPP2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPP2.getMandatoryConstraint());
            }
            
			Role role_fresh_OCP2 = new Role(role_fresh_OCP2_iri, ot_fresh_O, r_P2, otc_RoleOCP2); 
			
            if (role_fresh_OCP2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_OCP2.getMandatoryConstraint());
            }
			
			kf.addRole(role_fresh_CPP2);
			kf.addRole(role_fresh_OCP2);
			
			ArrayList<Role> r4 = new ArrayList();
			r4.add(role_fresh_CPP2);
			r4.add(role_fresh_OCP2);
			
			r_P2.setRoles(r4);
			
			kf.addRelationship(r_P1);
			kf.addRelationship(r_P2);
			
			Subsumption sub_rel_P_PAB1_fresh = new Subsumption(
					getAlphaNumericString(8),
					r_P1,
					r_fresh_PAB1
					);
			
			Subsumption sub_rel_P_PAB2_fresh = new Subsumption(
					getAlphaNumericString(8),
					r_P2,
					r_fresh_PAB2 
					);
			
			kf.addRelationship(sub_rel_P_PAB1_fresh);
			kf.addRelationship(sub_rel_P_PAB2_fresh);
		
		}
	}

}