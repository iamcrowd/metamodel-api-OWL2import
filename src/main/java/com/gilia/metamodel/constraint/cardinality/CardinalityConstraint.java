package com.gilia.metamodel.constraint.cardinality;

import com.gilia.metamodel.constraint.Constraint;

/**
 * Representation of the Cardinality constraint class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public abstract class CardinalityConstraint extends Constraint {

    public CardinalityConstraint() {
        super();
    }

    public CardinalityConstraint(String name) {
        super(name);
    }
}
