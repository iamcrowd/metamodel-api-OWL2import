package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class Ax1A extends AxToKFTools {

    public Ax1A() {
        super();
    }

    /**
     * Subclass(atom or conjunction of atoms, atom or disjunction of atoms) - A
     * \sqsubseteq B or (atom, atom)
     *
     * if OWLClassExpression is a FRESH concept, we generated a new IRI for such
     * FRESH by adding a URL
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type1AasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();
        String right_iri = right.asOWLClass().toStringID();

        if (isFresh(left)) {
            left_iri = left_iri = URI_NORMAL_CONCEPT + left.asOWLClass().toStringID();
        }
        if (isFresh(right)) {
            right_iri = URI_NORMAL_CONCEPT + right.asOWLClass().toStringID();
        }

        ObjectType ot_child = addObjectType(left_iri);
        ObjectType ot_parent = addObjectType(right_iri);

        kf.addEntity(ot_child);
        kf.addEntity(ot_parent);

        Subsumption sub = addSubsumption(kf, ot_parent, ot_child);
    }

}
