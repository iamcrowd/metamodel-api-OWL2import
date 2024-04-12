package com.gilia.owlimport.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.objecttype.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;

import static com.gilia.utils.Constants.URI_FAKE_TOP;
import static com.gilia.utils.Constants.URI_FRESH;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * This class implements the KF reconstruction of the COMPLEMENT_OF OWL 2 Axiom
 * 
 * @author gilia
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
    @SuppressWarnings("unchecked")
    public void complementOfasKF(Metamodel kf, OWLClassExpression left, OWLClassExpression right) {

        String left_iri = left.asOWLClass().toStringID();
        String left_frag = left.asOWLClass().getIRI().getFragment();
        ObjectType ot_left = addObjectType(left_iri);

        OWLClassExpression complement = ((OWLObjectComplementOf) right).getOperand();
        String complement_iri = complement.asOWLClass().toStringID();
        String complement_frag = complement.asOWLClass().getIRI().getFragment();
        ObjectType ot_complement = addObjectType(complement_iri);

        // fresh superclass of the left object type
        ObjectType ot_acomplementb = addObjectType(URI_FRESH + "/negation#" + complement_frag);
        // superclass of the pattern
        String top_iri = URI_FAKE_TOP;
        ObjectType top = addObjectType(top_iri);

        kf.addEntity(ot_left);
        kf.addEntity(ot_complement);
        kf.addEntity(ot_acomplementb);
        kf.addEntity(top);

        ArrayList<ObjectType> cc_list = new ArrayList();
        CompletenessConstraint cc = new CompletenessConstraint(getAlphaNumericString(8));

        ArrayList<ObjectType> disj_list = new ArrayList();
        DisjointObjectType disj = new DisjointObjectType(getAlphaNumericString(8));

        addSubsumption(kf, top, ot_acomplementb, cc, disj);
        cc_list.add(ot_acomplementb);
        disj_list.add(ot_acomplementb);

        addSubsumption(kf, ot_acomplementb, ot_left);

        addSubsumption(kf, top, ot_complement, cc, disj);
        cc_list.add(ot_complement);
        disj_list.add(ot_complement);

        cc.setEntities(cc_list);
        kf.addConstraint(cc);
        disj.setEntities(disj_list);
        kf.addConstraint(disj);

    }

}
