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
public class Ax1B extends AxToKFTools {

    public Ax1B() {
        super();
    }

    /**
     * Subclass(atom or conjunction of atoms, atom or disjunction of atoms) - A
     * \sqsubseteq B \sqcup C (atom, disjunction of atoms)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void type1BasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();

        ObjectType ot_left = addObjectType(left_iri);

        Set<OWLClassExpression> disjunctions = right.asDisjunctSet();
        ArrayList<String> union_iris = new ArrayList<>();
        for (OWLClassExpression d : disjunctions) {
            if (NormalForm.isAtom(d))
                union_iris.add(d.asOWLClass().getIRI().getFragment());
        }
        ObjectType ot_fresh = addObjectType(URI_FRESH + "/union#" + String.join("_", union_iris));

        ArrayList<ObjectType> cc_list = new ArrayList();
        CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));

        for (OWLClassExpression d : disjunctions) {
            if (NormalForm.isAtom(d)) {
                String d_iri = d.asOWLClass().toStringID();
                if (isFresh(d)) {
                    d_iri = URI_NORMAL_CONCEPT + d.asOWLClass().toStringID();
                }

                ObjectType ot = addObjectType(d_iri);

                kf.addEntity(ot);
                cc_list.add(ot);

                Subsumption sub_fresh = addSubsumption(kf, ot_fresh, ot, cc);
            }
        }

        cc.setEntities(cc_list);
        kf.addConstraint(cc);

        kf.addEntity(ot_left);
        kf.addEntity(ot_fresh);

        Subsumption sub = addSubsumption(kf, ot_fresh, ot_left);
    }

}
