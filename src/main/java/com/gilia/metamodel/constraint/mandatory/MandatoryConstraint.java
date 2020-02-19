package com.gilia.metamodel.constraint.mandatory;

import com.gilia.metamodel.constraint.Constraint;

/**
 * Representation of the Mandatory Constraint class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public abstract class MandatoryConstraint extends Constraint {
    public MandatoryConstraint() {
        super();
    }

    public MandatoryConstraint(String name) {
        super(name);
    }
}
