package com.gilia.owlimport;


import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;

import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLCardinalityRestrictionImpl;

/**
 * OWL Ax
 */
public class OWLAxForm {

    public OWLAxForm(){

    }

    public static boolean isAtom(OWLClassExpression e) {
		return e.isOWLClass() || e.isTopEntity() || e.isBottomEntity();
	}

	public static boolean isTypeAAtom(OWLClassExpression e) {
		return e.isOWLClass() || e.isTopEntity();
	}

	public static boolean isTypeBAtom(OWLClassExpression e) {
		return e.isOWLClass() || e.isBottomEntity();
	}

	public static boolean isConjunctionOfAtoms(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF)) {
			return false;
		}
		Set<OWLClassExpression> conjunctions = e.asConjunctSet();
		for (OWLClassExpression c : conjunctions) {
			if (!isAtom(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDisjunctionOfAtoms(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF)) {
			return false;
		}
		Set<OWLClassExpression> disjunctions = e.asDisjunctSet();
		for (OWLClassExpression d : disjunctions) {
			if (!isAtom(d)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check if e is an object complement expression
	 * 
	 * @param e
	 * @return
	 */
	public static boolean isComplementOfAtoms(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_COMPLEMENT_OF)) {
			return false;
		}
		
		OWLClassExpression complement = ((OWLObjectComplementOf) e).getOperand();

		if (isAtom(complement)) {
				return true;
		}
		return false;
	}	

	@SuppressWarnings("unchecked")
	public static boolean isExistentialOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM)) {
			return false;
		}

		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Data Some Values From expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	@SuppressWarnings("unchecked")
	public static boolean isExistentialOfData(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.DATA_SOME_VALUES_FROM)) {
			return false;
		}
		
		OWLDataRange filler = ((OWLQuantifiedRestrictionImpl<OWLDataRange>) e).getFiller();

		if (!filler.isOWLDatatype()) {
			return false;
		}
		return true;
	}
	

	/**
	 * Object Min Cardinality Expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	public static boolean isMinCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_MIN_CARDINALITY)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		OWLClassExpression filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Object Max Cardinality Expression
	 * 
	 * @param e
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static boolean isMaxCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_MAX_CARDINALITY)) {
			return false;
		}

		OWLClassExpression filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Object Exact Cardinality Expression
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isExactCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_EXACT_CARDINALITY)) {
			return false;
		}

		OWLClassExpression filler = ((OWLCardinalityRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Data Min Cardinality Expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	@SuppressWarnings("unchecked")
	public static boolean isDataMinCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.DATA_MIN_CARDINALITY)) {
			return false;
		}

		OWLDataRange filler = ((OWLCardinalityRestrictionImpl<OWLDataRange>) e).getFiller();

		if (!filler.isOWLDatatype()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Data Max Cardinality Expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	@SuppressWarnings("unchecked")
	public static boolean isDataMaxCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.DATA_MAX_CARDINALITY)) {
			return false;
		}
		
		OWLDataRange filler = ((OWLCardinalityRestrictionImpl<OWLDataRange>) e).getFiller();

		if (!filler.isOWLDatatype()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Data Exact Cardinality Expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	@SuppressWarnings("unchecked")
	public static boolean isDataExactCardinalityOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.DATA_EXACT_CARDINALITY)) {
			return false;
		}

		OWLDataRange filler = ((OWLCardinalityRestrictionImpl<OWLDataRange>) e).getFiller();

		if (!filler.isOWLDatatype()) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean isUniversalOfAtom(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM)) {
			return false;
		}

		OWLClassExpression filler = ((OWLQuantifiedRestrictionImpl<OWLClassExpression>) e).getFiller();

		if (!isAtom(filler)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Data All Values From Expression
	 * 
	 * @param e
	 * @return
	 * @implNote This function does not consider composed fillers. They can be only Datatypes
	 */
	@SuppressWarnings("unchecked")
	public static boolean isUniversalOfData(OWLClassExpression e) {
		if (!(e.getClassExpressionType() == ClassExpressionType.DATA_ALL_VALUES_FROM)) {
			return false;
		}
		
		OWLDataRange filler = ((OWLQuantifiedRestrictionImpl<OWLDataRange>) e).getFiller();

		if (!filler.isOWLDatatype()) {
			return false;
		}
		return true;
	}
   

}