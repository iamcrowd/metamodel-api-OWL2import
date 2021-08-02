package com.gilia.owlimporter.importer.axtoKF;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.constraint.disjointness.*;
import com.gilia.metamodel.entitytype.objecttype.*;
import com.gilia.metamodel.relationship.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import www.ontologyutils.normalization.*;

import static com.gilia.utils.Constants.*;

/**
 * This class implements the model based reconstructions of Normalised Axioms
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class AxToKFTools {

    public AxToKFTools() {

    }

    /**
     * Check of an OWLClassExpression is a fresh concept generated during
     * normalisation
     *
     * @param expr an OWLClassExpression
     * @return true if expr is a fresh concept
     */
    public boolean isFresh(OWLClassExpression expr) {
        if (expr.toString().contains("FRESH#")) {
            return true;
        } else {
            return false;
        }
    }

    protected ObjectType addObjectType(String iri) {
        if (iri.equals("owl:Thing")) {
            iri = URI_TOP;
        }
        return new ObjectType(iri);
    }

    private boolean _checkSubsumption(Metamodel kf, Entity parent, Entity child) {
        return !parent.getName().equals(child.getName()) && kf.getRelationship(_getSubsumptionName(parent, child)).isNameless();
    }

    private String _getSubsumptionName(Entity parent, Entity child) {
        return "Subsumption(" + parent.getName() + "," + child.getName() + ")";
    }

    private Subsumption _addSubsumption(Metamodel kf, Entity parent, Entity child, Subsumption subsumption) {
        if (_checkSubsumption(kf, parent, child)) {
            kf.addRelationship(subsumption);
            return subsumption;
        } else {
            return null;
        }
    }

    protected Subsumption addSubsumption(Metamodel kf, Entity parent, Entity child) {
        return _addSubsumption(kf, parent, child, new Subsumption(_getSubsumptionName(parent, child), parent, child));
    }

    protected Subsumption addSubsumption(Metamodel kf, Entity parent, Entity child, CompletenessConstraint cc) {
        return _addSubsumption(kf, parent, child, new Subsumption(_getSubsumptionName(parent, child), parent, child, cc));
    }

    protected Subsumption addSubsumption(Metamodel kf, Entity parent, Entity child, DisjointObjectType dc) {
        return _addSubsumption(kf, parent, child, new Subsumption(_getSubsumptionName(parent, child), parent, child, dc));
    }

    protected Subsumption addSubsumption(Metamodel kf, Entity parent, Entity child, List<Constraint> lc) {
        return _addSubsumption(kf, parent, child, new Subsumption(_getSubsumptionName(parent, child), parent, child, lc));
    }

    protected Subsumption addSubsumption(Metamodel kf, Entity parent, Entity child, CompletenessConstraint cc, DisjointObjectType dc) {
        return _addSubsumption(kf, parent, child, new Subsumption(_getSubsumptionName(parent, child), parent, child, cc, dc));
    }

}
