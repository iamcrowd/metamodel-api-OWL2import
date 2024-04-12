package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.cardinality.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import com.gilia.metamodel.role.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.TYPE2_DATA_EXACT_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.TYPE2_EXACT_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.URI_DOM;
import static com.gilia.utils.Constants.URI_FRESH;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;
import static com.gilia.utils.Constants.URI_RAN;
import static com.gilia.utils.Constants.URI_TOP;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class Ax2 extends AxToKFTools {

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
    public void type2asKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right, String TYPE) {

        // String left_iri = left.asOWLClass().toStringID();
        // if (isFresh(left)) {
        // left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID();
        // }

        // OWLClassExpression filler = null;
        // OWLPropertyExpression property = null;
        // String prop_iri = "";

        // if (TYPE == TYPE2_SUBCLASS_AXIOM) {
        // filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>)
        // right).getFiller();
        // property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>)
        // right).getProperty();
        // prop_iri = property.asOWLObjectProperty().toStringID();

        // } else if ((TYPE == TYPE2_MIN_CARD_AXIOM)
        // || (TYPE == TYPE2_MAX_CARD_AXIOM)
        // || (TYPE == TYPE2_EXACT_CARD_AXIOM)) {
        // filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>)
        // right).getFiller();
        // property = ((OWLCardinalityRestrictionImpl<OWLClassExpression>)
        // right).getProperty();
        // prop_iri = property.asOWLObjectProperty().toStringID();

        // }

        // if (NormalForm.isAtom(filler)) {

        // String filler_iri = filler.asOWLClass().toStringID();
        // if (isFresh(filler)) {
        // filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID();
        // }

        // // add subsumptions
        // String fresh_O = URI_TOP;
        // ObjectType ot_fresh_O = addObjectType(fresh_O);

        // ObjectType ot_left = addObjectType(left_iri);
        // ObjectType ot_filler = addObjectType(filler_iri);

        // Subsumption sub_fresh_leftORfiller = addSubsumption(kf, ot_fresh_O, ot_left);

        // Subsumption sub_fresh_leftORfiller_2 = addSubsumption(kf, ot_fresh_O,
        // ot_filler);

        // kf.addEntity(ot_fresh_O);
        // kf.addEntity(ot_left);
        // kf.addEntity(ot_filler);

        // // String fresh_C_PAB = URI_IMPORT_CONCEPT + "CPAB#" + left_iri + "$" +
        // // filler_iri;
        // String fresh_C_PAB = URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri + "$" +
        // filler_iri;
        // String fresh_C_P = prop_iri;

        // ObjectType ot_fresh_C_PAB = addObjectType(fresh_C_PAB);
        // ObjectType ot_C_P = addObjectType(fresh_C_P);

        // Subsumption sub_fresh_CP_CPAB = addSubsumption(kf, ot_C_P, ot_fresh_C_PAB);

        // kf.addEntity(ot_fresh_C_PAB);
        // kf.addEntity(ot_C_P);

        // // add fresh relationships
        // // String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#PAB1";
        // String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/" + prop_iri
        // + "-participation%" + left_iri;

        // // String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT +
        // getAlphaNumericString(8) +
        // // "#RCPAB1";
        // String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/participate-in%" + prop_iri;
        // // String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT +
        // getAlphaNumericString(8) +
        // // "#RAPAB1";
        // String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/has-one%" + left_iri;

        // ObjectTypeCardinality otc_RoleCPAB1 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");

        // Integer cardinality;
        // String min = "1";
        // String max = "*";

        // switch (TYPE) {
        // case TYPE2_SUBCLASS_AXIOM:
        // min = "1";
        // max = "*";

        // break;
        // case TYPE2_MIN_CARD_AXIOM:
        // cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>)
        // right).getCardinality();
        // if (cardinality == 1) {
        // min = "1";
        // max = "*";
        // } else if (cardinality > 1) {
        // min = cardinality.toString();
        // max = "*";
        // }

        // break;
        // case TYPE2_MAX_CARD_AXIOM:
        // cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>)
        // right).getCardinality();
        // min = "0";
        // max = cardinality.toString();

        // break;
        // case TYPE2_EXACT_CARD_AXIOM:
        // cardinality = ((OWLCardinalityRestrictionImpl<OWLClassExpression>)
        // right).getCardinality();
        // min = cardinality.toString();
        // max = cardinality.toString();

        // break;
        // case TYPE2_DATA_SUBCLASS_AXIOM:

        // break;
        // case TYPE2_DATA_MIN_CARD_AXIOM:

        // break;
        // case TYPE2_DATA_MAX_CARD_AXIOM:

        // break;
        // case TYPE2_DATA_EXACT_CARD_AXIOM:

        // break;

        // default:
        // min = "1";
        // max = "*";
        // break;
        // }

        // ObjectTypeCardinality otc_RoleAPAB1 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), min, max);

        // kf.addConstraint(otc_RoleCPAB1);
        // kf.addConstraint(otc_RoleAPAB1);

        // ArrayList<Entity> e1 = new ArrayList();
        // e1.add(ot_fresh_C_PAB);
        // e1.add(ot_left);

        // Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1);

        // Role role_fresh_CPAB1 = new Role(role_fresh_CPAB1_iri, ot_fresh_C_PAB,
        // r_fresh_PAB1, otc_RoleCPAB1);

        // if (role_fresh_CPAB1.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_CPAB1.getMandatoryConstraint());
        // }

        // Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_left, r_fresh_PAB1,
        // otc_RoleAPAB1);

        // if (role_fresh_APAB1.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_APAB1.getMandatoryConstraint());
        // }

        // kf.addRole(role_fresh_CPAB1);
        // kf.addRole(role_fresh_APAB1);

        // ArrayList<Role> r1 = new ArrayList();
        // r1.add(role_fresh_CPAB1);
        // r1.add(role_fresh_APAB1);

        // r_fresh_PAB1.setRoles(r1);

        // // String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#PAB2";
        // String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/" + prop_iri
        // + "-participation%" + filler_iri;

        // // String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT +
        // getAlphaNumericString(8) +
        // // "#RCPAB2";
        // String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/participate-in%" + prop_iri;
        // // String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT +
        // getAlphaNumericString(8) +
        // // "#RBPAB2";
        // String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/has-one%" + filler_iri;

        // ObjectTypeCardinality otc_RoleCPAB2 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
        // ObjectTypeCardinality otc_RoleBPAB2 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

        // kf.addConstraint(otc_RoleCPAB2);
        // kf.addConstraint(otc_RoleBPAB2);

        // ArrayList<Entity> e2 = new ArrayList();
        // e2.add(ot_fresh_C_PAB);
        // e2.add(ot_filler);

        // Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e2);

        // Role role_fresh_CPAB2 = new Role(role_fresh_CPAB2_iri, ot_fresh_C_PAB,
        // r_fresh_PAB2, otc_RoleCPAB2);

        // if (role_fresh_CPAB2.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_CPAB2.getMandatoryConstraint());
        // }

        // Role role_fresh_BPAB2 = new Role(role_fresh_BPAB2_iri, ot_filler,
        // r_fresh_PAB2, otc_RoleBPAB2);

        // if (role_fresh_BPAB2.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_BPAB2.getMandatoryConstraint());
        // }

        // kf.addRole(role_fresh_CPAB2);
        // kf.addRole(role_fresh_BPAB2);

        // ArrayList<Role> r2 = new ArrayList();
        // r2.add(role_fresh_CPAB2);
        // r2.add(role_fresh_BPAB2);

        // r_fresh_PAB2.setRoles(r2);

        // kf.addRelationship(r_fresh_PAB1);
        // kf.addRelationship(r_fresh_PAB2);

        // // add original relationships
        // // String rel_P1_iri = prop_iri + "_" + getAlphaNumericString(8);
        // String rel_P1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" +
        // prop_iri + "-participation%"
        // + left_iri;

        // // String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#RCPP1";
        // String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/participate-in%" + prop_iri;
        // // String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#ROCP1";
        // String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/has-one%" + fresh_O;

        // ObjectTypeCardinality otc_RoleCPP1 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
        // ObjectTypeCardinality otc_RoleOCP1 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

        // kf.addConstraint(otc_RoleCPP1);
        // kf.addConstraint(otc_RoleOCP1);

        // ArrayList<Entity> e3 = new ArrayList();
        // e3.add(ot_C_P);
        // e3.add(ot_fresh_O);

        // Relationship r_P1 = new Relationship(rel_P1_iri, e3);

        // Role role_fresh_CPP1 = new Role(role_fresh_CPP1_iri, ot_C_P, r_P1,
        // otc_RoleCPP1);

        // if (role_fresh_CPP1.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_CPP1.getMandatoryConstraint());
        // }

        // Role role_fresh_OCP1 = new Role(role_fresh_OCP1_iri, ot_fresh_O, r_P1,
        // otc_RoleOCP1);

        // if (role_fresh_OCP1.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_OCP1.getMandatoryConstraint());
        // }

        // kf.addRole(role_fresh_CPP1);
        // kf.addRole(role_fresh_OCP1);

        // ArrayList<Role> r3 = new ArrayList();
        // r3.add(role_fresh_CPP1);
        // r3.add(role_fresh_OCP1);

        // r_P1.setRoles(r3);

        // // String rel_P2_iri = prop_iri + "_" + getAlphaNumericString(8);
        // String rel_P2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" +
        // prop_iri + "-participation%"
        // + filler_iri;

        // // String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#RCPP2";
        // String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/participate-in%" + prop_iri;
        // // String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8)
        // +
        // // "#ROCP2";
        // String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) +
        // "/has-one%" + fresh_O;

        // ObjectTypeCardinality otc_RoleCPP2 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
        // ObjectTypeCardinality otc_RoleOCP2 = new
        // ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

        // kf.addConstraint(otc_RoleCPP2);
        // kf.addConstraint(otc_RoleOCP2);

        // Relationship r_P2 = new Relationship(rel_P2_iri, e3);

        // Role role_fresh_CPP2 = new Role(role_fresh_CPP2_iri, ot_C_P, r_P2,
        // otc_RoleCPP2);

        // if (role_fresh_CPP2.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_CPP2.getMandatoryConstraint());
        // }

        // Role role_fresh_OCP2 = new Role(role_fresh_OCP2_iri, ot_fresh_O, r_P2,
        // otc_RoleOCP2);

        // if (role_fresh_OCP2.hasMandatoryConstraint()) {
        // kf.addConstraint(role_fresh_OCP2.getMandatoryConstraint());
        // }

        // kf.addRole(role_fresh_CPP2);
        // kf.addRole(role_fresh_OCP2);

        // ArrayList<Role> r4 = new ArrayList();
        // r4.add(role_fresh_CPP2);
        // r4.add(role_fresh_OCP2);

        // r_P2.setRoles(r4);

        // kf.addRelationship(r_P1);
        // kf.addRelationship(r_P2);

        // Subsumption sub_rel_P_PAB1_fresh = addSubsumption(kf, r_P1, r_fresh_PAB1);

        // Subsumption sub_rel_P_PAB2_fresh = addSubsumption(kf, r_P2, r_fresh_PAB2);
        // }
    }

    /**
     * Subclass(atom, exists property atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type2ImportedAsKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right, String TYPE) {

        String left_iri = left.asOWLClass().toStringID();

        OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
        OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty();

        String prop_iri = property.asOWLObjectProperty().toStringID();
        String prop_frag = property.asOWLObjectProperty().getIRI().getFragment();

        if (NormalForm.isAtom(filler)) {
            String filler_iri = filler.asOWLClass().toStringID();
            String filler_frag = filler.asOWLClass().getIRI().getFragment();

            // Object types

            // Dom \exists P B
            String fresh_left_iri = URI_FRESH + "/dom/exists#" + prop_frag + "_" + filler_frag;
            ObjectType ot_fresh_left = addObjectType(fresh_left_iri);

            // A
            ObjectType ot_left = addObjectType(left_iri);
            // B
            ObjectType ot_filler = addObjectType(filler_iri);

            // Dom
            String fresh_dom_iri = URI_DOM;
            ObjectType ot_fresh_dom = addObjectType(fresh_dom_iri);
            // Ran
            String fresh_ran_iri = URI_RAN;
            ObjectType ot_fresh_ran = addObjectType(fresh_ran_iri);

            kf.addEntity(ot_left);
            kf.addEntity(ot_fresh_left);
            kf.addEntity(ot_filler);
            kf.addEntity(ot_fresh_dom);
            kf.addEntity(ot_fresh_ran);

            // Object types Subsumption
            Subsumption sub_fresh_dom = addSubsumption(kf, ot_fresh_dom, ot_fresh_left);
            Subsumption sub_left_fresh = addSubsumption(kf, ot_fresh_left, ot_left);

            // add P relationship
            String rel_P_iri = prop_iri;

            // IRIs for roles of P
            String role_P_1_iri = prop_iri + "-role-1";
            String role_P_2_iri = prop_iri + "-role-2";

            // add cardinalities for P
            ObjectTypeCardinality otc_role_P_1_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");
            ObjectTypeCardinality otc_role_P_2_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_role_P_1_DomRan);
            kf.addConstraint(otc_role_P_2_DomRan);

            ArrayList<Entity> PL = new ArrayList();
            PL.add(ot_fresh_dom);
            PL.add(ot_fresh_ran);

            // Relationship r_P = new Relationship(rel_P_iri, PL);
            Relationship r_P = addRelationship(kf, rel_P_iri, PL);

            if (r_P != null) {
                // Add P roles and set mandatory
                Role role_P_1 = new Role(role_P_1_iri, ot_fresh_dom, r_P, otc_role_P_1_DomRan);

                Role role_P_2 = new Role(role_P_2_iri, ot_fresh_ran, r_P, otc_role_P_2_DomRan);
                if (role_P_2.hasMandatoryConstraint()) {
                    kf.addConstraint(role_P_2.getMandatoryConstraint());
                }

                kf.addRole(role_P_1);
                kf.addRole(role_P_2);

                ArrayList<Role> Pres = new ArrayList();
                Pres.add(role_P_1);
                Pres.add(role_P_2);

                r_P.setRoles(Pres);
                // kf.addRelationship(r_P);
            } else {
                r_P = kf.getRelationship(rel_P_iri);
            }

            // P1 (\exists P B)
            String rel_P1_iri = URI_FRESH + "/exists#" + prop_frag + "_" + filler_frag;
            // IRIs for roles of P1
            String rel_fresh_P11_iri = URI_FRESH + "/exists#" + prop_frag + "-role-1" + "_" + filler_frag;
            String rel_fresh_P12_iri = URI_FRESH + "/exists#" + prop_frag + "-role-2" + "_" + filler_frag;

            // add cardinalities for P1
            ObjectTypeCardinality otc_role_P_11_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");
            ObjectTypeCardinality otc_role_P_12_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_role_P_11_DomRan);
            kf.addConstraint(otc_role_P_12_DomRan);

            ArrayList<Entity> P1L = new ArrayList();
            P1L.add(ot_fresh_left);
            P1L.add(ot_filler);

            // Relationship r_P1 = new Relationship(rel_P1_iri, P1L);
            Relationship r_P1 = addRelationship(kf, rel_P1_iri, P1L);

            if (r_P1 != null) {

                // Add P1 roles and set mandatory
                Role role_P_11 = new Role(rel_fresh_P11_iri, ot_fresh_left, r_P1, otc_role_P_11_DomRan);
                if (role_P_11.hasMandatoryConstraint()) {
                    kf.addConstraint(role_P_11.getMandatoryConstraint());
                }

                Role role_P_12 = new Role(rel_fresh_P12_iri, ot_filler, r_P1, otc_role_P_12_DomRan);
                if (role_P_12.hasMandatoryConstraint()) {
                    kf.addConstraint(role_P_12.getMandatoryConstraint());
                }

                kf.addRole(role_P_11);
                kf.addRole(role_P_12);

                ArrayList<Role> Pres1 = new ArrayList();
                Pres1.add(role_P_11);
                Pres1.add(role_P_12);

                r_P1.setRoles(Pres1);
                // kf.addRelationship(r_P1);
            } else {
                r_P1 = kf.getRelationship(rel_P1_iri);
            }

            // Object types Subsumption
            String sub_include_rels_uri = URI_FRESH + "/subsumption#" + prop_frag + "_" + filler_frag + "_" + prop_frag;
            if (!kf.doesEntityExists(sub_include_rels_uri)) {
                Subsumption sub_P1P = new Subsumption(sub_include_rels_uri, r_P, r_P1);
                kf.addRelationship(sub_P1P);
            }
        }
    }

}
