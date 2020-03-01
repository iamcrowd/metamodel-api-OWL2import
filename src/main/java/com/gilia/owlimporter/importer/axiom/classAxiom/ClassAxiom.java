package com.gilia.owlimporter.importer.axiom;

import com.gilia.owlimporter.importer.ClassExpression;

/**
 * ClassAxiom := SubClassOf | EquivalentClasses | DisjointClasses | DisjointUnion
 * @author gab
 *
 */
public abstract class ClassAxiom extends ClassExpression {

    public ClassAxiom() {
        super();
    }
    
}
