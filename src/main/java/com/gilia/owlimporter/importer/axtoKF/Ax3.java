package com.gilia.owlimporter.importer.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.constraint.cardinality.*;
import com.gilia.metamodel.constraint.disjointness.*;
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
public class Ax3 extends AxToKFTools {

    public Ax3() {
        super();
    }

    /**
     * Subclass(atom, forall property atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type3asKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();
        if (isFresh(left)) {
            left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID();
        }

        OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
        OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty();

        String prop_iri = property.asOWLObjectProperty().toStringID();

        ArrayList<ObjectType> dot_list = new ArrayList();
        DisjointObjectType dot = new DisjointObjectType(getAlphaNumericString(8));

        if (NormalForm.isAtom(filler)) {
            String filler_iri = filler.asOWLClass().toStringID();
            if (isFresh(filler)) {
                filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID();
            }

            //add subsumptions
            String fresh_O = URI_TOP;
            ObjectType ot_fresh_O = addObjectType(fresh_O);

            // String fresh_left_iri = left_iri + "p";
            String fresh_left_iri = URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri;
            ObjectType ot_fresh_left = addObjectType(fresh_left_iri);

            ObjectType ot_left = addObjectType(left_iri);

            // String complement_left_iri = left_iri + "c";
            String complement_left_iri = URI_IMPORT_CONCEPT + "COMPLEMENT%" + URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri;
            ObjectType ot_complement_left = addObjectType(complement_left_iri);

            ObjectType ot_filler = addObjectType(filler_iri);

            dot_list.add(ot_fresh_left);
            dot_list.add(ot_complement_left);

            dot.setEntities(dot_list);

            kf.addEntity(ot_fresh_O);
            kf.addEntity(ot_left);
            kf.addEntity(ot_fresh_left);
            kf.addEntity(ot_complement_left);
            kf.addEntity(ot_filler);
            kf.addConstraint(dot);

            Subsumption sub_fresh_leftORcomplement = addSubsumption(kf, ot_fresh_O, ot_fresh_left, dot);

            Subsumption sub_fresh_leftORcomplement_2 = addSubsumption(kf, ot_fresh_O, ot_complement_left, dot);

            Subsumption sub_left_fresh_left = addSubsumption(kf, ot_fresh_left, ot_left);

            //add subsumption CP
            // String fresh_C_PAB = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#CPAB";
            String fresh_C_PAB = URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri + "$" + filler_iri;
            // String fresh_C_PABc = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#CPABc";
            String fresh_C_PABc = URI_IMPORT_CONCEPT + "COMPLEMENT%" + URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri + "$" + filler_iri;
            String fresh_C_P = prop_iri;

            ObjectType ot_fresh_C_PAB = addObjectType(fresh_C_PAB);
            ObjectType ot_fresh_C_PABc = addObjectType(fresh_C_PABc);
            ObjectType ot_C_P = addObjectType(fresh_C_P);

            ArrayList<ObjectType> cc_list = new ArrayList();
            CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));

            cc_list.add(ot_fresh_C_PAB);
            cc_list.add(ot_fresh_C_PABc);

            cc.setEntities(cc_list);

            kf.addConstraint(cc);
            kf.addEntity(ot_fresh_C_PAB);
            kf.addEntity(ot_fresh_C_PABc);
            kf.addEntity(ot_C_P);

            Subsumption sub_fresh_CP_CPAB = addSubsumption(kf, ot_C_P, ot_fresh_C_PAB, cc);

            Subsumption sub_fresh_CP_CPABc = addSubsumption(kf, ot_C_P, ot_fresh_C_PABc, cc);

            //add fresh relationships
            // String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB1";
            String rel_fresh_PAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + fresh_left_iri;

            // String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB1";
            String role_fresh_CPAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RAPAB1";
            String role_fresh_APAB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + fresh_left_iri;

            ObjectTypeCardinality otc_RoleCPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleAPAB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAB1);
            kf.addConstraint(otc_RoleAPAB1);

            ArrayList<Entity> e1 = new ArrayList();
            e1.add(ot_fresh_C_PAB);
            e1.add(ot_fresh_left);

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

            //
            // String rel_fresh_PAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAcB1";
            String rel_fresh_PAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + complement_left_iri;

            // String role_fresh_CPAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAcB1";
            String role_fresh_CPAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_AcPAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RAcPAcB1";
            String role_fresh_AcPAcB1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + complement_left_iri;

            ObjectTypeCardinality otc_RoleCPAcB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleAcPAcB1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAcB1);
            kf.addConstraint(otc_RoleAcPAcB1);

            ArrayList<Entity> e2 = new ArrayList();
            e2.add(ot_fresh_C_PABc);
            e2.add(ot_complement_left);

            Relationship r_fresh_PAcB1 = new Relationship(rel_fresh_PAcB1_iri, e2);

            Role role_fresh_CPAcB1 = new Role(role_fresh_CPAcB1_iri, ot_fresh_C_PABc, r_fresh_PAcB1, otc_RoleCPAcB1);

            if (role_fresh_CPAcB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_CPAcB1.getMandatoryConstraint());
            }

            Role role_fresh_AcPAcB1 = new Role(role_fresh_AcPAcB1_iri, ot_complement_left, r_fresh_PAcB1, otc_RoleAcPAcB1);

            if (role_fresh_AcPAcB1.hasMandatoryConstraint()) {
                kf.addConstraint(role_fresh_AcPAcB1.getMandatoryConstraint());
            }

            kf.addRole(role_fresh_CPAcB1);
            kf.addRole(role_fresh_AcPAcB1);

            ArrayList<Role> r2 = new ArrayList();
            r2.add(role_fresh_CPAcB1);
            r2.add(role_fresh_AcPAcB1);

            r_fresh_PAcB1.setRoles(r2);

            //
            // String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#PAB2";
            String rel_fresh_PAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            // String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPAB2";
            String role_fresh_CPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RBPAB2";
            String role_fresh_BPAB2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + filler_iri;

            ObjectTypeCardinality otc_RoleCPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleBPAB2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPAB2);
            kf.addConstraint(otc_RoleBPAB2);

            ArrayList<Entity> e3 = new ArrayList();
            e3.add(ot_fresh_C_PAB);
            e3.add(ot_filler);

            Relationship r_fresh_PAB2 = new Relationship(rel_fresh_PAB2_iri, e3);

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

            ArrayList<Role> r3 = new ArrayList();
            r3.add(role_fresh_CPAB2);
            r3.add(role_fresh_BPAB2);

            r_fresh_PAB2.setRoles(r3);

            kf.addRelationship(r_fresh_PAB1);
            kf.addRelationship(r_fresh_PAcB1);
            kf.addRelationship(r_fresh_PAB2);

            //add original relationships
            // String rel_P1_iri = prop_iri + "1"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P1";
            String rel_P1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + left_iri;

            // String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP1";
            String role_fresh_CPP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP1";
            String role_fresh_OCP1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + fresh_O;

            ObjectTypeCardinality otc_RoleCPP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleOCP1 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPP1);
            kf.addConstraint(otc_RoleOCP1);

            ArrayList<Entity> e4 = new ArrayList();
            e4.add(ot_C_P);
            e4.add(ot_fresh_O);

            Relationship r_P1 = new Relationship(rel_P1_iri, e4);

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

            ArrayList<Role> r4 = new ArrayList();
            r4.add(role_fresh_CPP1);
            r4.add(role_fresh_OCP1);

            r_P1.setRoles(r4);

            // String rel_P2_iri = prop_iri + "2"; //"http://crowd.fi.uncoma.edu.ar/IMPORT" + getAlphaNumericString(8) + "#P2";
            String rel_P2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/" + prop_iri + "-participation%" + filler_iri;

            // String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#RCPP2";
            String role_fresh_CPP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/participate-in%" + prop_iri;
            // String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "#ROCP2";
            String role_fresh_OCP2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + "/has-one%" + fresh_O;

            ObjectTypeCardinality otc_RoleCPP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "1");
            ObjectTypeCardinality otc_RoleOCP2 = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_RoleCPP2);
            kf.addConstraint(otc_RoleOCP2);

            Relationship r_P2 = new Relationship(rel_P2_iri, e4);

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

            ArrayList<Role> r5 = new ArrayList();
            r5.add(role_fresh_CPP2);
            r5.add(role_fresh_OCP2);

            r_P2.setRoles(r5);

            kf.addRelationship(r_P1);
            kf.addRelationship(r_P2);

            Subsumption sub_rel_P_PAB1_fresh = addSubsumption(kf, r_P1, r_fresh_PAB1);

            Subsumption sub_rel_P_PAcB1_fresh = addSubsumption(kf, r_P1, r_fresh_PAcB1);

            Subsumption sub_rel_P_PAB2_fresh = addSubsumption(kf, r_P2, r_fresh_PAB2);
        }
    }
    
    /**
     * Subclass(atom, forall property atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type3ImportedAsKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();
        if (isFresh(left)) {
            left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID();
        }

        OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getFiller();
        OWLPropertyExpression property = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) right).getProperty();

        String prop_iri = property.asOWLObjectProperty().toStringID();

        if (NormalForm.isAtom(filler)) {
            String filler_iri = filler.asOWLClass().toStringID();
            if (isFresh(filler)) {
                filler_iri = URI_NORMAL_CONCEPT + filler.asOWLClass().toStringID();
            }

            // Object types
        
            // String fresh_left_iri = left_iri + "p";
            String fresh_left_iri = URI_IMPORT_CONCEPT + prop_iri + "%" + left_iri;
            ObjectType ot_fresh_left = addObjectType(fresh_left_iri);
        
            //A
            ObjectType ot_left = addObjectType(left_iri);
            //B
            ObjectType ot_filler = addObjectType(filler_iri);
        
            //Dom
            String fresh_dom_iri = URI_IMPORT_CONCEPT + "%" + "Dom";
            ObjectType ot_fresh_dom = addObjectType(fresh_dom_iri);
            //Ran
            String fresh_ran_iri = URI_IMPORT_CONCEPT + "%" + "Ran";
            ObjectType ot_fresh_ran = addObjectType(fresh_ran_iri);

            kf.addEntity(ot_left);
            kf.addEntity(ot_fresh_left);
            kf.addEntity(ot_filler);
            kf.addEntity(ot_fresh_dom);
            kf.addEntity(ot_fresh_ran);
        
            // Object types Subsumption
            Subsumption sub_fresh_dom = addSubsumption(kf, ot_fresh_dom, ot_fresh_left);
            Subsumption sub_left_fresh = addSubsumption(kf, ot_fresh_left, ot_left);
        
            //add P relationship
            String rel_P_iri = prop_iri;
        
            // IRIs for roles of P
            String role_P_1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + ".1";
            String role_P_2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + ".2";

            // add cardinalities for P
            ObjectTypeCardinality otc_role_P_1_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");
            ObjectTypeCardinality otc_role_P_2_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

            kf.addConstraint(otc_role_P_1_DomRan);
            kf.addConstraint(otc_role_P_2_DomRan);
        
            ArrayList<Entity> PL = new ArrayList();
            PL.add(ot_fresh_dom);
            PL.add(ot_fresh_ran);

            Relationship r_P = new Relationship(rel_P_iri, PL);
        
            // Add P roles and set mandatory
            Role role_P_1 = new Role(role_P_1_iri, ot_fresh_dom, r_P, otc_role_P_1_DomRan);
            if (role_P_1.hasMandatoryConstraint()) {
               kf.addConstraint(role_P_1.getMandatoryConstraint());
            }
        
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
            kf.addRelationship(r_P);
        
			// P1
			String rel_P1_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "1";
			// IRIs for roles of P1
			String rel_fresh_P11_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "1.1";
			String rel_fresh_P12_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "1.2";

			// add cardinalities for P1
			ObjectTypeCardinality otc_role_P_11_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");
			ObjectTypeCardinality otc_role_P_12_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

			kf.addConstraint(otc_role_P_11_DomRan);
			kf.addConstraint(otc_role_P_12_DomRan);

			ArrayList<Entity> P1L = new ArrayList();
			P1L.add(ot_fresh_left);
			P1L.add(ot_filler);

			Relationship r_P1 = new Relationship(rel_P1_iri, P1L);

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
			kf.addRelationship(r_P1);

			// P2
			String rel_P2_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "2";
			// IRIs for roles of P2
			String rel_fresh_P21_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "2.1";
			String rel_fresh_P22_iri = URI_IMPORT_CONCEPT + getAlphaNumericString(8) + prop_iri + "2.2";

			// add cardinalities for P1
			ObjectTypeCardinality otc_role_P_21_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "1", "*");
			ObjectTypeCardinality otc_role_P_22_DomRan = new ObjectTypeCardinality(getAlphaNumericString(8), "0", "*");

			kf.addConstraint(otc_role_P_21_DomRan);
			kf.addConstraint(otc_role_P_22_DomRan);

			ArrayList<Entity> P2L = new ArrayList();
			P2L.add(ot_left);
			P2L.add(ot_filler);

			Relationship r_P2 = new Relationship(rel_P2_iri, P2L);

			// Add P1 roles and set mandatory
			Role role_P_21 = new Role(rel_fresh_P21_iri, ot_fresh_left, r_P2, otc_role_P_21_DomRan);
			if (role_P_21.hasMandatoryConstraint()) {
				kf.addConstraint(role_P_21.getMandatoryConstraint());
			}

			Role role_P_22 = new Role(rel_fresh_P22_iri, ot_filler, r_P2, otc_role_P_22_DomRan);
			if (role_P_22.hasMandatoryConstraint()) {
				kf.addConstraint(role_P_22.getMandatoryConstraint());
			}

			kf.addRole(role_P_21);
			kf.addRole(role_P_22);

			ArrayList<Role> Pres2 = new ArrayList();
			Pres2.add(role_P_21);
			Pres2.add(role_P_22);

			r_P2.setRoles(Pres2);
			kf.addRelationship(r_P2);

			// Object types Subsumption
			Subsumption sub_P1P = new Subsumption(URI_IMPORT_CONCEPT + getAlphaNumericString(8), r_P, r_P1);
			Subsumption sub_P2P1 = new Subsumption(URI_IMPORT_CONCEPT + getAlphaNumericString(8), r_P1, r_P2);
			kf.addRelationship(sub_P1P);
			kf.addRelationship(sub_P2P1);

        }
    }

}
