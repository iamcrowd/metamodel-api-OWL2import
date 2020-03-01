package com.gilia.owlimporter.importer;

import com.gilia.owlimporter.importer.Axiom;

/**
ClassExpression :=
    Class |
    ObjectIntersectionOf | ObjectUnionOf | ObjectComplementOf | ObjectOneOf |
    ObjectSomeValuesFrom | ObjectAllValuesFrom | ObjectHasValue | ObjectHasSelf |
    ObjectMinCardinality | ObjectMaxCardinality | ObjectExactCardinality |
    DataSomeValuesFrom | DataAllValuesFrom | DataHasValue |
    DataMinCardinality | DataMaxCardinality | DataExactCardinality
 * @author gab
 *
 */
public abstract class ClassExpression extends Axiom{

    public ClassExpression() {}
    
}