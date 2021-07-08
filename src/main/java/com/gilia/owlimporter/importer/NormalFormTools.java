package com.gilia.owlimporter.importer;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
//import com.sun.tools.javac.util.List;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.role.Role;

import static com.gilia.utils.Utils.getAlphaNumericString;
import com.google.common.base.CaseFormat;
//import com.sun.tools.javac.util.List;

import uk.ac.manchester.cs.owl.owlapi.OWLCardinalityRestrictionImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLQuantifiedRestrictionImpl;

import com.gilia.exceptions.EmptyOntologyException;

import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.normalization.Normalization;
import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.normalization.NormalForm;

import static com.gilia.utils.Constants.TYPE2_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_EXACT_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_SUBCLASS_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MIN_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_MAX_CARD_AXIOM;
import static com.gilia.utils.Constants.TYPE2_DATA_EXACT_CARD_AXIOM;

import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;
import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;
import java.util.EmptyStackException;

/**
 * This class identifies each normal form in ontology normalised being imported
 * and creates the respective KF primitives
 *
 * @see NormalForm from ontologyutils dependency
 *
 * @author gbraun
 *
 */
public class NormalFormTools {

    private OWLOntology copy;
    private OWLOntology naive;
    private OWLOntology unsupported;

    private long nOfAx1A;
    private long nOfAx1B;
    private long nOfAx1C;
    private long nOfAx1D;
    private long nOfAx2A;
    private long nOfAx2B;
    private long nOfAx2C;
    private long nOfAx2D;
    private long nOfAx3;
    private long nOfAx4;
    private long nOfAx2DT;
    private long nOfAx3DT;
    private long nOfAx4DT;

    /**
     *  * A TBox axiom in normal form can be of one of four types:
     *
     * Type 1: Subclass(atom or conjunction of atoms, atom or disjunction of
     * atoms) 1a atom atom 1b atom disj Type 2: Subclass(atom, exists property
     * atom) Type 3: Subclass(atom, forall property atom) Type 4:
     * Subclass(exists property atom, atom)
     *
     */
    public NormalFormTools() {
        this.copy = Utils.newEmptyOntology();
        this.naive = Utils.newEmptyOntology();
        this.unsupported = Utils.newEmptyOntology();
    }

    public OWLOntology getNaive() {
        return this.naive;
    }

    public OWLOntology getUnsupportedAxioms() {
        return this.unsupported;
    }

    public long getnOfAx1A() {
        return nOfAx1A;
    }

    public long getnOfAx1B() {
        return nOfAx1B;
    }

    public long getnOfAx1C() {
        return nOfAx1C;
    }

    public long getnOfAx1D() {
        return nOfAx1D;
    }

    public long getnOfAx2A() {
        return nOfAx2A;
    }

    public long getnOfAx2B() {
        return nOfAx2B;
    }

    public long getnOfAx2C() {
        return nOfAx2C;
    }

    public long getnOfAx2D() {
        return nOfAx2D;
    }

    public long getnOfAx3() {
        return nOfAx3;
    }

    public long getnOfAx4() {
        return nOfAx4;
    }

    /**
     * This function prepares input ontology to be normalised and classifies
     * axioms that could not be normalised
     *
     * @param ontology
     */
    private void prepareOntology(OWLOntology ontology) {
        this.copy.addAxioms(ontology.axioms());

        Stream<OWLAxiom> tBoxAxioms = this.copy.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach((ax) -> {
            this.copy.remove(ax);

            try {
                this.copy.addAxioms(NormalizationTools.asSubClassOfAxioms(ax));
            } catch (Exception f) {
                //System.out.println("Unsupported axioms:" + ax.toString());
                this.unsupported.addAxiom(ax);
            }

        });
    }

    /**
     * check if a new collection of normalised axioms is entailed by the
     * original ontology with an extended signature
     *
     * @param ax_n collection of normalised axioms
     * @param axs_fresh set of fresh to extend ontology signature
     */
    private void isEntailedNorm(Collection<OWLSubClassOfAxiom> ax_n, Set<OWLAxiom> axs_fresh) {
        OWLOntology temp = Utils.newEmptyOntology();
        temp.add(ax_n);
        this.copy.addAxioms(axs_fresh);
        OWLReasoner reasoner = Utils.getHermitReasoner(this.copy);
        assert (temp.axioms().allMatch(ax1 -> reasoner.isEntailed(ax1)));
    }

    /**
     * Only axioms type 1 (A) (atom, atom) are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type1ANormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeOneSubClassAxiom(left, right)) {
                                            // atom atom
                                            if (NormalForm.isAtom(left) && NormalForm.isAtom(right)) {
                                                Ax1A ax1AasKF = new Ax1A();
                                                ax1AasKF.type1AasKF(kf, left, right);
                                            }
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 1 (B) (atom, disj) are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type1BNormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeOneSubClassAxiom(left, right)) {
                                            // atom disj
                                            if (NormalForm.isAtom(left) && NormalForm.isDisjunctionOfAtoms(right)) {
                                                Ax1B ax1BasKF = new Ax1B();
                                                ax1BasKF.type1BasKF(kf, left, right);
                                            }
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 1 (C) (conj, atom) are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type1CNormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeOneSubClassAxiom(left, right)) {
                                            // conj atom
                                            if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isAtom(right)) {
                                                Ax1C ax1CasKF = new Ax1C();
                                                ax1CasKF.type1CasKF(kf, left, right);
                                            }
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 1 (D) (conj, disj) are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type1DNormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeOneSubClassAxiom(left, right)) {
                                            // conj atom
                                            if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isDisjunctionOfAtoms(right)) {
                                                Ax1D ax1DasKF = new Ax1D();
                                                ax1DasKF.type1DasKF(kf, left, right);
                                            }
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 2 are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type2NormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeTwoSubClassAxiom(left, right)) {	//Object
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_SUBCLASS_AXIOM);
                                        } else if (NormalForm.typeTwoMinCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_MIN_CARD_AXIOM);
                                        } else if (NormalForm.typeTwoMaxCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_MAX_CARD_AXIOM);
                                        } else if (NormalForm.typeTwoExactCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_EXACT_CARD_AXIOM);
                                        } else if (NormalForm.typeTwoDataSubClassAxiom(left, right)) { //Data
                                            //System.out.println("typeTwoDataSubClassAxiom");
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_SUBCLASS_AXIOM);	
                                        } else if (NormalForm.typeTwoDataMinCardAxiom(left, right)) {
                                            //System.out.println("typeTwoDataMinCardAxiom");
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_MIN_CARD_AXIOM);	
                                        } else if (NormalForm.typeTwoDataMaxCardAxiom(left, right)) {
                                            //System.out.println("typeTwoDataMaxCardAxiom");
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_MAX_CARD_AXIOM);	
                                        } else if (NormalForm.typeTwoDataExactCardAxiom(left, right)) {
                                            //System.out.println("typeTwoDataExactCardAxiom");
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_EXACT_CARD_AXIOM);	
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 3 are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type3NormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom, forall property filler)
                                        if (NormalForm.typeThreeSubClassAxiom(left, right)) {
                                            Ax3 ax3asKF = new Ax3();
                                            ax3asKF.type3asKF(kf, left, right);
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * Only axioms type 4 are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     */
    public void type4NormalisedasKF(Metamodel kf, OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);
                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {
                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeFourSubClassAxiom(left, right)) {
                                            Ax4 ax4asKF = new Ax4();
                                            ax4asKF.type4asKF(kf, left, right);
                                        }
                                    } else {
                                        //System.out.println("Do nothing:" + ax.toString());
                                    }
                                });
                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });
    }

    /**
     * All type of normalised axioms are imported
     *
     * Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     * @return a normalised ontology to be imported
     */
    public void asKF(Metamodel kf, OWLOntology ontology) {

        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        this.prepareOntology(ontology);

        this.naive.addAxioms(ontology.rboxAxioms(Imports.EXCLUDED));
        this.naive.addAxioms(ontology.aboxAxioms(Imports.EXCLUDED));

        OWLDataFactory df = OWLManager.getOWLDataFactory();

        ontology.signature(Imports.EXCLUDED).forEach((entity) -> {
            OWLAxiom entityAxiom = df.getOWLDeclarationAxiom(entity);
            this.naive.addAxiom(entityAxiom);
        });

        Set<OWLAxiom> tBoxAxiomsCopy = this.copy.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        tBoxAxiomsCopy.forEach(
                (ax) -> {
                    try {
                        Collection<OWLSubClassOfAxiom> ax_n = NormalizationTools.normalizeSubClassAxiom((OWLSubClassOfAxiom) ax);

                        isEntailedNorm(ax_n, FreshAtoms.getFreshAtomsEquivalenceAxioms());

                        ax_n.forEach(
                                (ax_sub) -> {
                                    if (NormalForm.isNormalFormTBoxAxiom(ax_sub)) {

                                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax_sub).getSubClass();
                                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax_sub).getSuperClass();

                                        // Subclass(atom or conjunction of atoms, atom or disjunction of atoms)
                                        // A \sqsubseteq B or A \sqcap B \sqsubseteq C or 
                                        if (NormalForm.typeOneSubClassAxiom(left, right)) {
                                            // atom atom
                                            if (NormalForm.isAtom(left) && NormalForm.isAtom(right)) {
                                                Ax1A ax1AasKF = new Ax1A();
                                                ax1AasKF.type1AasKF(kf, left, right);
                                                this.nOfAx1A++;
                                                // atom disj	
                                            } else if (NormalForm.isAtom(left) && NormalForm.isDisjunctionOfAtoms(right)) {
                                                Ax1B ax1BasKF = new Ax1B();
                                                ax1BasKF.type1BasKF(kf, left, right);
                                                this.nOfAx1B++;
                                                // conj atom	
                                            } else if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isAtom(right)) {
                                                Ax1C ax1CasKF = new Ax1C();
                                                ax1CasKF.type1CasKF(kf, left, right);
                                                this.nOfAx1C++;
                                                // conj disj
                                            } else if (NormalForm.isConjunctionOfAtoms(left) && NormalForm.isDisjunctionOfAtoms(right)) {
                                                Ax1D ax1DasKF = new Ax1D();
                                                ax1DasKF.type1DasKF(kf, left, right);
                                                this.nOfAx1D++;
                                            } else {
                                                throw new EmptyStackException();
                                            }
                                        } else if (NormalForm.typeTwoSubClassAxiom(left, right)) {	//Object		 								
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_SUBCLASS_AXIOM);
                                            this.nOfAx2A++;
                                        } else if (NormalForm.typeTwoMinCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_MIN_CARD_AXIOM);
                                            this.nOfAx2B++;
                                        } else if (NormalForm.typeTwoMaxCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_MAX_CARD_AXIOM);
                                            this.nOfAx2C++;
                                        } else if (NormalForm.typeTwoExactCardAxiom(left, right)) {
                                            Ax2 ax2asKF = new Ax2();
                                            ax2asKF.type2asKF(kf, left, right, TYPE2_EXACT_CARD_AXIOM);
                                            this.nOfAx2D++;
                                        } else if (NormalForm.typeTwoDataSubClassAxiom(left, right)) { //Data
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_SUBCLASS_AXIOM);}
                                            throw new EmptyStackException();
                                        } else if (NormalForm.typeTwoDataMinCardAxiom(left, right)) {
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_MIN_CARD_AXIOM);	
                                            throw new EmptyStackException();
                                        } else if (NormalForm.typeTwoDataMaxCardAxiom(left, right)) {
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_MAX_CARD_AXIOM);	
                                            throw new EmptyStackException();
                                        } else if (NormalForm.typeTwoDataExactCardAxiom(left, right)) {
                                            //this.type2asKF(kf, left, right, TYPE2_DATA_EXACT_CARD_AXIOM);
                                            throw new EmptyStackException();
                                        } else if (NormalForm.typeThreeSubClassAxiom(left, right)) {
                                            Ax3 ax3asKF = new Ax3();
                                            ax3asKF.type3asKF(kf, left, right);
                                            this.nOfAx3++;
                                        } else if (NormalForm.typeFourSubClassAxiom(left, right)) {
                                            Ax4 ax4asKF = new Ax4();
                                            ax4asKF.type4asKF(kf, left, right);
                                            this.nOfAx4++;
                                        } else {
                                            throw new EmptyStackException();
                                        }

                                    } else {
                                        //System.out.println("Do nothing:" + ax_sub.toString());
                                        throw new EmptyStackException();
                                    }
                                });

                        this.naive.addAxioms(ax_n);
                    } catch (Exception fex) {
                        //System.out.println("Unsupported axioms:" + ax.toString());
                        this.unsupported.addAxiom(ax);
                    }
                });

    }

}
