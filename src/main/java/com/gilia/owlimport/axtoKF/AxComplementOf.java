package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;
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
public class AxComplementOf extends AxToKFTools {

    public AxComplementOf() {
        super();
    }

    /**
     * Subclass(atom, not atom)
     *
     * @param kf
     * @param left
     * @param right
     */
    public void complementOfasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();
        ObjectType ot_left = addObjectType(left_iri);

        OWLClassExpression complement = ((OWLObjectComplementOf) right).getOperand();
        String complement_iri = complement.asOWLClass().toStringID();
        ObjectType ot_complement = addObjectType(complement_iri);

        // fresh superclass of the left object type
        ObjectType ot_acomplementb = addObjectType(URI_IMPORT_CONCEPT + "NOT");
        // superclass of the pattern
        String top_iri = URI_TOP;
        ObjectType top = addObjectType(top_iri);

        kf.addEntity(ot_left);
        kf.addEntity(ot_complement);
        kf.addEntity(ot_acomplementb);
        kf.addEntity(top);

        ArrayList<ObjectType> cc_list = new ArrayList();
        CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));

        ArrayList<ObjectType> disj_list = new ArrayList();
        DisjointObjectType disj = new DisjointObjectType(getAlphaNumericString(8));

        Subsumption sub_complement_top = addSubsumption(kf, top, ot_acomplementb, cc, disj);
        cc_list.add(ot_acomplementb);
        disj_list.add(ot_acomplementb);

        Subsumption sub_left = addSubsumption(kf, ot_acomplementb, ot_left);

        Subsumption sub_right_top = addSubsumption(kf, top, ot_complement, cc, disj);
        cc_list.add(ot_complement);
        disj_list.add(ot_complement);

        cc.setEntities(cc_list);
        kf.addConstraint(cc);
        disj.setEntities(disj_list);
        kf.addConstraint(disj);

    }

}
