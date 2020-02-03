package com.gilia.metamodel.constraint.cardinality;

import com.gilia.metamodel.constraint.Constraint;

public abstract class CardinalityConstraint extends Constraint {

    public CardinalityConstraint() {
        super();
    }

    public CardinalityConstraint(String name) {
        super(name);
    }
}
