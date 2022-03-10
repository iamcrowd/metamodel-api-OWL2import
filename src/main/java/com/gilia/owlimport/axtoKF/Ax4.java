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

import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;
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
public class Ax4 extends AxToKFTools {

    public Ax4() {
        super();
    }

    /**
     * Subclass(exists property atom, atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type4asKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String right_iri = right.asOWLClass().toStringID();
        if (isFresh(right)) {
            right_iri = URI_NORMAL_CONCEPT + right.asOWLClass().toStringID();
        }

        OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) left).getFiller();
        OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) left).getProperty();

        String prop_iri = property.asOWLObjectProperty().toStringID();

        if (NormalForm.isAtom(filler)) {
            String filler_iri = filler.asOWLClass().toStringID();
            if (isFresh(filler)) {
                filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID();
            }

            //add subsumptions
            String fresh_O = URI_TOP;
            ObjectType ot_fresh_O = addObjectType(fresh_O);

            ObjectType ot_right = addObjectType(right_iri);
            ObjectType ot_filler = addObjectType(filler_iri);

            Subsumption sub_fresh_rightORfiller = addSubsumption(kf, ot_fresh_O, ot_right);

            Subsumption sub_fresh_rightORfiller_2 = addSubsumption(kf, ot_fresh_O, ot_filler);

            kf.addEntity(ot_fresh_O);
            kf.addEntity(ot_right);
            kf.addEntity(ot_filler);

            // String fresh_C_PAB = URI_IMPORT_CONCEPT + "CPAB#" + right_iri + "$" + filler_iri;
            String fresh_C_PAB = URI_IMPORT_CONCEPT + prop_iri + "%" + right_iri + "$" + filler_iri;
            String fresh_C_P = prop_iri; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#CP";

            ObjectType ot_fresh_C_PAB = addObjectType(fresh_C_PAB);
            ObjectType ot_C_P = addObjectType(fresh_C_P);

            Subsumption sub_fresh_CP_CPAB = addSubsumption(kf, ot_C_P, ot_fresh_C_PAB);

            kf.addEntity(ot_fresh_C_PAB);
            kf.addEntity(ot_C_P);

            //add fresh relationships
            // String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB1";
            String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + right_iri;

            // String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB1";
            String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RAPAB1";
            String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + right_iri;

            ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAB1);
            kf.addConstraint(otc_RoleAPAB1);

            ArrayList<Entity> e1 = new ArrayList();
            e1.add(ot_fresh_C_PAB);
            e1.add(ot_right);

            Relationship r_fresh_PAB1 = new Relationship(rel_fresh_PAB1_iri, e1);

            Role role_fresh_CPAB1 = new Role(role_fresh_CPAB1_iri, ot_fresh_C_PAB, r_fresh_PAB1, otc_RoleCPAB1);

            if (role_fresh_CPAB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPAB1.getMandatoryConstraint());
            }

            Role role_fresh_APAB1 = new Role(role_fresh_APAB1_iri, ot_right, r_fresh_PAB1, otc_RoleAPAB1);

            if (role_fresh_APAB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_APAB1.getMandatoryConstraint());
            }

            kf.addRole(role_fresh_CPAB1);
            kf.addRole(role_fresh_APAB1);

            ArrayList<Role> r1 = new ArrayList();
            r1.add(role_fresh_CPAB1);
            r1.add(role_fresh_APAB1);

            r_fresh_PAB1.setRoles(r1);

            // String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB2";
            String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            // String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB2";
            String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RBPAB2";
            String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + filler_iri;

            ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");

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
            // String rel_P1_iri = prop_iri + 1; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P1";
            String rel_P1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + right_iri;

            // String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP1";
            String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP1";
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

            // String rel_P2_iri = prop_iri + 2; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P2";
            String rel_P2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            // String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP2";
            String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP2";
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

            Subsumption sub_rel_P_PAB1_fresh = addSubsumption(kf, r_P1, r_fresh_PAB1);

            Subsumption sub_rel_P_PAB2_fresh = addSubsumption(kf, r_P2, r_fresh_PAB2);
        }
    }

}
