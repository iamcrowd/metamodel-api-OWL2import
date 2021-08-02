package com.gilia.owlimporter.importer.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

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
        if (isFresh(right)) {
            right_iri = URI_NORMAL_CONCEPT + right.asOWLClass().toStringID();
        }

        ObjectType ot_right = addObjectType(right_iri);

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
        ObjectType ot_fresh = addObjectType(URI_IMPORT_CONCEPT + "INTERSECTION%" + c_iris);

        for (OWLClassExpression c : conjunctions) {
            if (NormalForm.isAtom(c)) {
                String c_iri = c.asOWLClass().toStringID();
                if (isFresh(c)) {
                    c_iri = URI_NORMAL_CONCEPT + c.asOWLClass().toStringID();
                }
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
