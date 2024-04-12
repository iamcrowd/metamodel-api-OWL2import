package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.URI_FRESH;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class Ax1D extends AxToKFTools {

    public Ax1D() {
        super();
    }

    /**
     * Subclass(atom or conjunction of atoms, atom or disjunction of atoms) - A
     * \sqcap B \sqsubseteq C \sqcup D (conjunction of atoms, disjunction of
     * atoms)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type1DasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        Set<OWLClassExpression> disjunctions = right.asDisjunctSet();
        ArrayList<String> union_iris = new ArrayList<>();
        for (OWLClassExpression d : disjunctions) {
            if (NormalForm.isAtom(d))
                union_iris.add(d.asOWLClass().getIRI().getFragment());
        }
        ObjectType ot_fresh_d = addObjectType(URI_FRESH + "/union#" + String.join("_", union_iris));

        ArrayList<ObjectType> cc_list = new ArrayList();
        CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));

        for (OWLClassExpression d : disjunctions) {
            if (NormalForm.isAtom(d)) {
                String d_iri = d.asOWLClass().toStringID();
                ObjectType ot = addObjectType(d_iri);

                kf.addEntity(ot);
                cc_list.add(ot);

                Subsumption sub_fresh_d = addSubsumption(kf, ot_fresh_d, ot, cc);
            }
        }

        cc.setEntities(cc_list);
        kf.addConstraint(cc);
        kf.addEntity(ot_fresh_d);

        Set<OWLClassExpression> conjunctions = left.asConjunctSet();
        ArrayList<String> intersection_iris = new ArrayList<>();
        for (OWLClassExpression c : conjunctions) {
            if (NormalForm.isAtom(c)) {
                intersection_iris.add(c.asOWLClass().getIRI().getFragment());
            }
        }
        ObjectType ot_fresh_c = addObjectType(URI_FRESH + "/intersection#" + String.join("_", intersection_iris));

        for (OWLClassExpression c : conjunctions) {
            if (NormalForm.isAtom(c)) {
                String c_iri = c.asOWLClass().toStringID();
                ObjectType ot = addObjectType(c_iri);

                kf.addEntity(ot);

                Subsumption sub_fresh_c = addSubsumption(kf, ot, ot_fresh_c);
            }
        }

        kf.addEntity(ot_fresh_c);

        Subsumption sub = addSubsumption(kf, ot_fresh_d, ot_fresh_c);
    }

}
