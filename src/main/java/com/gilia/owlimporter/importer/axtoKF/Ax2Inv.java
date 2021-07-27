package com.gilia.owlimporter.importer.axtoKF;

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
import static com.gilia.utils.Constants.URI_TOP;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class Ax2Inv extends AxToKFTools {

    public Ax2Inv() {
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

        String left_iri = left.asOWLClass().toStringID();
        if (isFresh(left)) {
            left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID();
        }

        OWLClassExpression filler = null;
        OWLObjectPropertyExpression property = null;
        String prop_iri = "";

        if (TYPE == TYPE2_SUBCLASS_AXIOM) {
            filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
            property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty().asObjectPropertyExpression();
            prop_iri = property.getInverseProperty().asOWLObjectProperty().toStringID();

        } else if ((TYPE == TYPE2_MIN_CARD_AXIOM)
                || (TYPE == TYPE2_MAX_CARD_AXIOM)
                || (TYPE == TYPE2_EXACT_CARD_AXIOM)) {
            filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getFiller();
            property = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) right).getProperty().asObjectPropertyExpression();
            prop_iri = property.getInverseProperty().asOWLObjectProperty().toStringID();

        }

        if (NormalForm.isAtom(filler)) {

            String filler_iri = filler.asOWLClass().toStringID();
            if (isFresh(filler)) {
                filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID();
            }

            //add subsumptions
            String fresh_O = URI_TOP;
            ObjectType ot_fresh_O = new ObjectType(fresh_O);

            ObjectType ot_left = new ObjectType(left_iri);
            ObjectType ot_filler = new ObjectType(filler_iri);

            if (kf.getRelationship("Subsumption(" + ot_fresh_O.getName() + "," + ot_left.getName() + ")").isNameless()) {
                Subsumption sub_fresh_leftORfiller = new Subsumption(
                        "Subsumption(" + ot_fresh_O.getName() + "," + ot_left.getName() + ")",
                        ot_fresh_O,
                        ot_left);
                kf.addRelationship(sub_fresh_leftORfiller);
            }

            if (kf.getRelationship("Subsumption(" + ot_fresh_O.getName() + "," + ot_filler.getName() + ")").isNameless()) {
                Subsumption sub_fresh_leftORfiller_2 = new Subsumption(
                        "Subsumption(" + ot_fresh_O.getName() + "," + ot_filler.getName() + ")",
                        ot_fresh_O,
                        ot_filler);
                kf.addRelationship(sub_fresh_leftORfiller_2);
            }

            kf.addEntity(ot_fresh_O);
            kf.addEntity(ot_left);
            kf.addEntity(ot_filler);

            String fresh_C_PAB = URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri + "$" + filler_iri;
            String fresh_C_PBA = URI_IMPORT_CONCEPT + prop_iri + "%" + filler_iri + "$" + left_iri;
            String fresh_C_P = prop_iri;

            ObjectType ot_fresh_C_PAB = new ObjectType(fresh_C_PAB);
            ObjectType ot_fresh_C_PBA = new ObjectType(fresh_C_PBA);
            ObjectType ot_C_P = new ObjectType(fresh_C_P);

            if (kf.getRelationship("Subsumption(" + ot_C_P.getName() + "," + ot_fresh_C_PAB.getName() + ")").isNameless()) {
                Subsumption sub_fresh_CP_CPAB = new Subsumption(
                        "Subsumption(" + ot_C_P.getName() + "," + ot_fresh_C_PAB.getName() + ")",
                        ot_C_P,
                        ot_fresh_C_PAB);
                kf.addRelationship(sub_fresh_CP_CPAB);
            }

            kf.addEntity(ot_fresh_C_PAB);
            kf.addEntity(ot_fresh_C_PBA);
            kf.addEntity(ot_C_P);

            //add fresh relationships CPAB
            //add PAB_1
            String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + left_iri;

            String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + left_iri;

            ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAB1);
            kf.addConstraint(otc_RoleAPAB1);

            ArrayList<Entity> e1AB = new ArrayList();
            e1AB.add(ot_fresh_C_PAB);
            e1AB.add(ot_left);

            Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1AB);

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

            ArrayList<Role> r1AB = new ArrayList();
            r1AB.add(role_fresh_CPAB1);
            r1AB.add(role_fresh_APAB1);

            r_fresh_PAB1.setRoles(r1AB);

            //add PAB_2
            String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + filler_iri;

            ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAB2);
            kf.addConstraint(otc_RoleBPAB2);

            ArrayList<Entity> e2AB = new ArrayList();
            e2AB.add(ot_fresh_C_PAB);
            e2AB.add(ot_filler);

            Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e2AB);

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

            ArrayList<Role> r2AB = new ArrayList();
            r2AB.add(role_fresh_CPAB2);
            r2AB.add(role_fresh_BPAB2);

            r_fresh_PAB2.setRoles(r2AB);

            kf.addRelationship(r_fresh_PAB1);
            kf.addRelationship(r_fresh_PAB2);
            
            //add fresh relationships CPBA
            //add PBA_1
            String rel_fresh_PBA1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            String role_fresh_CPBA1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_APBA1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + filler_iri;

            ObjectTypeCardinality otc_RoleCPBA1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleAPBA1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPBA1);
            kf.addConstraint(otc_RoleAPBA1);

            ArrayList<Entity> e1BA = new ArrayList();
            e1BA.add(ot_fresh_C_PBA);
            e1BA.add(ot_filler);

            Relationship r_fresh_PBA1 = new Relationship(rel_fresh_PBA1_iri, e1BA);

            Role role_fresh_CPBA1 = new Role(role_fresh_CPBA1_iri, ot_fresh_C_PBA, r_fresh_PBA1, otc_RoleCPBA1);

            if (role_fresh_CPBA1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPBA1.getMandatoryConstraint());
            }

            Role role_fresh_APBA1 = new Role(role_fresh_APBA1_iri, ot_filler, r_fresh_PBA1, otc_RoleAPBA1);

            if (role_fresh_APBA1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_APBA1.getMandatoryConstraint());
            }

            kf.addRole(role_fresh_CPBA1);
            kf.addRole(role_fresh_APBA1);

            ArrayList<Role> r1BA = new ArrayList();
            r1BA.add(role_fresh_CPBA1);
            r1BA.add(role_fresh_APBA1);

            r_fresh_PBA1.setRoles(r1BA);

            //add PBA_2
            String rel_fresh_PBA2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + left_iri;

            String role_fresh_CPBA2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_BPBA2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + left_iri;

            ObjectTypeCardinality otc_RoleCPBA2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            
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
                    } else if (cardinality > 1) {
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

            ObjectTypeCardinality otc_RoleBPBA2 = new ObjectTypeCardinality(getAlphaNumericString(8), min, max);

            kf.addConstraint(otc_RoleCPBA2);
            kf.addConstraint(otc_RoleBPBA2);

            ArrayList<Entity> e2BA = new ArrayList();
            e2BA.add(ot_fresh_C_PBA);
            e2BA.add(ot_left);

            Relationship r_fresh_PBA2 = new Relationship(rel_fresh_PBA2_iri, e2BA);

            Role role_fresh_CPBA2 = new Role(role_fresh_CPBA2_iri, ot_fresh_C_PBA, r_fresh_PBA2, otc_RoleCPBA2);

            if (role_fresh_CPBA2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPBA2.getMandatoryConstraint());
            }

            Role role_fresh_BPBA2 = new Role(role_fresh_BPBA2_iri, ot_left, r_fresh_PBA2, otc_RoleBPBA2);

            if (role_fresh_BPBA2.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_BPBA2.getMandatoryConstraint());
            }

            kf.addRole(role_fresh_CPBA2);
            kf.addRole(role_fresh_BPBA2);

            ArrayList<Role> r2BA = new ArrayList();
            r2BA.add(role_fresh_CPBA2);
            r2BA.add(role_fresh_BPBA2);

            r_fresh_PBA2.setRoles(r2BA);

            kf.addRelationship(r_fresh_PBA1);
            kf.addRelationship(r_fresh_PBA2);

            //add relationship PABBA
            String rel_PABBA_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/inverse%" + prop_iri;

            String role_fresh_ABBA_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/is-inverse%-";
            String role_fresh_BAAB_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/is-inverse%-";

            ObjectTypeCardinality otc_RoleABBA = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleBAAB = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");

            kf.addConstraint(otc_RoleABBA);
            kf.addConstraint(otc_RoleBAAB);

            ArrayList<Entity> eABBA = new ArrayList();
            eABBA.add(ot_fresh_C_PAB);
            eABBA.add(ot_fresh_C_PBA);
            
            Relationship r_PABBA = new Relationship(rel_PABBA_iri, eABBA);

            Role role_fresh_ABBA = new Role(role_fresh_ABBA_iri, ot_fresh_C_PAB, r_PABBA, otc_RoleABBA);

            if (role_fresh_ABBA.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_ABBA.getMandatoryConstraint());
            }

            Role role_fresh_BAAB = new Role(role_fresh_BAAB_iri, ot_fresh_C_PBA, r_PABBA, otc_RoleBAAB);

            if (role_fresh_BAAB.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_BAAB.getMandatoryConstraint());
            }

            kf.addRole(role_fresh_ABBA);
            kf.addRole(role_fresh_BAAB);

            ArrayList<Role> rPABBA = new ArrayList();
            rPABBA.add(role_fresh_ABBA);
            rPABBA.add(role_fresh_BAAB);

            r_PABBA.setRoles(rPABBA);

            kf.addRelationship(r_PABBA);
            
            //add original relationships
            //add P_1
            String rel_P1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + left_iri;

            String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + fresh_O;

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

            //add P_2
            String rel_P2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + fresh_O;

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

            //add subsumptions between PAB_1 and P_1
            if (kf.getRelationship("Subsumption(" + r_P1.getName() + "," + r_fresh_PAB1.getName() + ")").isNameless()) {
                Subsumption sub_rel_P_PAB1_fresh = new Subsumption(
                        "Subsumption(" + r_P1.getName() + "," + r_fresh_PAB1.getName() + ")",
                        r_P1,
                        r_fresh_PAB1
                );
                kf.addRelationship(sub_rel_P_PAB1_fresh);
            }

            //add subsumptions between PAB_2 and P_2
            if (kf.getRelationship("Subsumption(" + r_P2.getName() + "," + r_fresh_PAB2.getName() + ")").isNameless()) {
                Subsumption sub_rel_P_PAB2_fresh = new Subsumption(
                        "Subsumption(" + r_P2.getName() + "," + r_fresh_PAB2.getName() + ")",
                        r_P2,
                        r_fresh_PAB2
                );
                kf.addRelationship(sub_rel_P_PAB2_fresh);
            }

            //add subsumptions between PBA_1 and P_2
            if (kf.getRelationship("Subsumption(" + r_P2.getName() + "," + r_fresh_PBA1.getName() + ")").isNameless()) {
                Subsumption sub_rel_P_PBA1_fresh = new Subsumption(
                        "Subsumption(" + r_P2.getName() + "," + r_fresh_PBA1.getName() + ")",
                        r_P2,
                        r_fresh_PBA1
                );
                kf.addRelationship(sub_rel_P_PBA1_fresh);
            }
            
            //add subsumptions between PBA_2 and P_1
            if (kf.getRelationship("Subsumption(" + r_P1.getName() + "," + r_fresh_PBA2.getName() + ")").isNameless()) {
                Subsumption sub_rel_P_PBA2_fresh = new Subsumption(
                        "Subsumption(" + r_P1.getName() + "," + r_fresh_PBA2.getName() + ")",
                        r_P1,
                        r_fresh_PBA2
                );
                kf.addRelationship(sub_rel_P_PBA2_fresh);
            }
        }
    }

}
