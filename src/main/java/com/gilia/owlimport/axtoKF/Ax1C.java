package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.URI_FRESH;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class Ax1C extends AxToKFTools {

    public Ax1C() {
        super();
    }

    /**
     * Subclass(atom or conjunction of atoms, atom or disjunction of atoms) - A
     * \sqcap B \sqsubseteq C or (conjuction of atoms, atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type1CasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String right_iri = right.asOWLClass().toStringID();

        ObjectType ot_right = addObjectType(right_iri);

        Set<OWLClassExpression> conjunctions = left.asConjunctSet();
        ArrayList<String> intersection_iris = new ArrayList<>();
        for (OWLClassExpression c : conjunctions) {
            if (NormalForm.isAtom(c)) {
                intersection_iris.add(c.asOWLClass().getIRI().getFragment());
            }
        }
        ObjectType ot_fresh = addObjectType(URI_FRESH + "/intersection#" + String.join("_", intersection_iris));

        for (OWLClassExpression c : conjunctions) {
            if (NormalForm.isAtom(c)) {
                String c_iri = c.asOWLClass().toStringID();
                ObjectType ot = addObjectType(c_iri);

                kf.addEntity(ot);

                Subsumption sub_fresh = addSubsumption(kf, ot, ot_fresh);
            }
        }

        kf.addEntity(ot_right);
        kf.addEntity(ot_fresh);

        Subsumption sub = addSubsumption(kf, ot_right, ot_fresh);
    }

}
