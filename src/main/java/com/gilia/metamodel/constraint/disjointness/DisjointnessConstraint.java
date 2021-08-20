package com.gilia.metamodel.constraint.disjointness;

import com.gilia.metamodel.constraint.Constraint;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;


/**
 * Representation of the Disjointness constraint class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class DisjointnessConstraint extends Constraint {

    public DisjointnessConstraint() {
        super();
    }

    public DisjointnessConstraint(String name) {
        super(name);
    }
    
    /**
     * English verbalisation of Disjointness constraints
     * @return
     */
    public void toCNLen() {
  	  this.cnl.setSubject(this.name);
  	  this.cnl.setVerb("is");
  	  this.cnl.setObject("a Disjointness constraint");
    }
    
}
