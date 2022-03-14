package com.gilia.owlimport;

import static com.gilia.utils.Constants.TYPE2_SUBCLASS_AXIOM;
import static com.gilia.utils.ImportUtils.validateOWL;

import com.gilia.builder.metabuilder.*;
import com.gilia.metamodel.*;
import com.gilia.owlimport.axtoKF.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.json.simple.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;
import org.springframework.web.multipart.*;

import uk.ac.manchester.cs.jfact.*;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import openllet.owlapi.OpenlletReasonerFactory;

import www.ontologyutils.toolbox.*;

/**
 * Allow translate OWL to KF metamodel by applying axioms translations.
 * Preserving the semantics of the original ontology.
 */
@SuppressWarnings("unchecked")
public class OWLImporter {

    private Metamodel metamodel;
    private MetaConverter converter;
    private OWLOntology ontology;
    private OWLOntology supported;
    private OWLOntology unsupported;
    private OWLOntologyManager manager;
    private boolean reasoning;
    private static final OWLReasonerFactory reasonerFactoryFact = new JFactFactory();
    private static final OWLReasonerFactory reasonerFactoryPellet = new OpenlletReasonerFactory();
    private List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();

    private List<OWLClassExpression> objpe = new ArrayList<>();
    private List<OWLAxiom> forallax = new ArrayList<>();

    private JSONObject metrics;

    public OWLImporter(boolean reasoning) {
        this.reset();
        this.converter = new MetaConverter();
        this.manager = OWLManager.createOWLOntologyManager();
        this.reasoning = reasoning;
    }

    /**
     * Reset resultant metamodel, supported and unsupported axioms for load a new
     * ontology.
     */
    public void reset() {
        this.metamodel = new Metamodel();
        this.supported = Utils.newEmptyOntology();
        this.unsupported = Utils.newEmptyOntology();
        this.metrics = new JSONObject();

        // reset metrics parameters
        this.metrics.put("translationTime", null);
        this.metrics.put("supportedAxiomsCount", 0);
        this.metrics.put("unsupportedAxiomsCount", 0);
    }

    /**
     *
     * @param iri a String containing an Ontology URI.
     */
    public void load(IRI iri) {
        try {
            this.reset();
            validateOWL(iri);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(iri);
        } catch (Exception e) {
            System.out.println("Error loading ontology with iri: " + iri + ". (" + e.getMessage() + ")");
        }
    }

    /**
     *
     * @param iri a String containing an Ontology RDF/XML file.
     */
    public void load(String string) {
        try {
            this.reset();
            this.ontology = this.manager
                    .loadOntologyFromOntologyDocument(new ByteArrayInputStream(string.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error loading ontology with String: " + string + ". (" + e.getMessage() + ")");
        }
    }

    /**
     *
     * @param path a String containing a file path to an Ontology File.
     */
    public void loadFromPath(String path) {
        try {
            this.reset();
            File file = new File(path);
            validateOWL(file);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
        } catch (Exception e) {
            System.out.println("Error loading ontology with path: " + path + ". (" +
                    e.getMessage() + ")");
        }
    }

    /**
     *
     * @param multipartFile a MultiparFile from a FormData containing an Ontology
     *                      File.
     */
    public void load(MultipartFile multipartFile) {
        try {
            this.reset();
            File file = new File("src/main/resources/temporalOWL.tmp");
            try (OutputStream os = new FileOutputStream(file)) {
                os.write(multipartFile.getBytes());
            }
            validateOWL(file);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
        } catch (Exception e) {
            System.out.println("Error loading ontology with multipart file. (" + e.getMessage() + ")");
        }
    }

    /**
     * Executes a reasoner over the input ontology to get inferred axioms.
     */
    private void precompute() {
        try {
            OWLReasoner reasoner = reasonerFactoryPellet.createReasoner(this.ontology);
            //OWLReasoner reasoner = reasonerFactoryFact.createReasoner(this.ontology);

            // List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
            this.gens.add(new InferredSubClassAxiomGenerator());
            this.gens.add(new InferredClassAssertionAxiomGenerator());
            this.gens.add(new InferredDisjointClassesAxiomGenerator());
            this.gens.add(new InferredEquivalentClassAxiomGenerator());
            this.gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
            this.gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            this.gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
            this.gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
            this.gens.add(new InferredPropertyAssertionGenerator());
            this.gens.add(new InferredSubDataPropertyAxiomGenerator());
            this.gens.add(new InferredSubObjectPropertyAxiomGenerator());

            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, this.gens);
            OWLDataFactory df = OWLManager.getOWLDataFactory();
            iog.fillOntology(df, this.ontology);
        } catch (Exception e) {
            System.out.println("Error precomputing ontology. (" + e.getMessage() + ")");
        }
    }

    /**
     * Patterns
     * atom -> atom
     * atom -> disjunction
     * atom -> complementof atom
     * atom -> exists property atom
     * atom -> forall property atom
     * 
     */
    private void patternify(Metamodel kf, OWLAxiom axiom, OWLClassExpression left, OWLClassExpression right) {
        // atom -> atom
        if (OWLAxForm.isAtom(left) && OWLAxForm.isAtom(right)) {
            Ax1A ax1A = new Ax1A();
            ax1A.type1AasKF(kf, left, right);
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isDisjunctionOfAtoms(right)) {
            // atom -> disjunction
            Ax1B ax1B = new Ax1B();
            ax1B.type1BasKF(kf, left, right);
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isComplementOfAtoms(right)) {
            // atom -> complementOf atom
            // removing \bottom -> complement_of top (\top -> complement_of \bottom)
            OWLClassExpression complement = ((OWLObjectComplementOf) right).getOperand();
            if (!(left.isOWLNothing() && complement.isOWLThing()) &&
                !(complement.isOWLNothing() && left.isOWLThing())){
                    AxComplementOf axComp = new AxComplementOf();
                    axComp.complementOfasKF(kf, left, right);
            }
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isExistentialOfAtom(right)) {
            // atom -> exists property atom
            OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) right).getProperty();
            OWLObjectProperty namedProperty = property.getNamedProperty();

            if (property.isNamed()) {
                OWLClassExpression exist_expr = new OWLObjectSomeValuesFromImpl(property,
                        new OWLClassImpl(IRI.create("http://www.w3.org/2002/07/owl#Thing")));
                this.objpe.add(exist_expr);
                Ax2 ax2asKF = new Ax2();
                ax2asKF.type2ImportedAsKF(kf, left, right, TYPE2_SUBCLASS_AXIOM);
            } else {
                throw new EmptyStackException();
            }
        } else if (OWLAxForm.isExistentialOfAtom(right) && OWLAxForm.isAtom(left)) {
            // exists property atom -> ... this pattern only collects the exists axioms of
            // the ontology
            OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) left).getProperty();
            OWLObjectProperty namedProperty = property.getNamedProperty();

            if (property.isNamed()) {
                this.objpe.add(left);
            } else {
                throw new EmptyStackException();
            }
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isUniversalOfAtom(right)) {
            // atom -> forall property atom
            OWLObjectPropertyExpression property = ((OWLObjectAllValuesFrom) right).getProperty();
            OWLObjectProperty namedProperty = property.getNamedProperty();

            if (property.isNamed()) {
                this.forallax.add(axiom);
            } else {
                throw new EmptyStackException();
            }
        } else {
            this.unsupported.addAxiom(axiom);
            this.metrics.put("unsupportedAxiomsCount", ((int) this.metrics.get("unsupportedAxiomsCount")) + 1);
        }
        this.supported.addAxioms(axiom);
        this.metrics.put("supportedAxiomsCount", ((int) this.metrics.get("supportedAxiomsCount")) + 1);
    }

    /**
     * Translation to KF metamodel. It does reasoning first and then translate each
     * supported axiom and register both: supported and unsupported axioms.
     *
     * @todo objpe should be a set for avoiding repretitions.
     * 
     * @implNote still missing: 
     * union -> atom could be rewritten as atomic subclasses,
     * disjoint union (c, c1, ... cn) as equivalent(c, union_of(c1,..,cn)) and disjoint(c1,...,cn)
     * min, max, exact cardinalities
     */
    public void translate() {
        long start, end;
        start = Calendar.getInstance().getTimeInMillis();

        if (this.reasoning) {
            // reason over the input ontology
            this.precompute();
        }
        // get all tbox axioms
        Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        // iterate each axiom
        tboxAxioms.forEach(axiom -> {
            try {
                // determine if axiom is of type SubClassOf
                System.out.println(axiom.toString());

                if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    // get left and right expressions (SubClass -> SuperClass)
                    OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
                    OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();

                    this.patternify(this.metamodel, axiom, left, right);

                } else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
                    Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
                    subClassOfAxioms = ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms();

                    subClassOfAxioms.forEach(ax -> {
                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();

                        this.patternify(this.metamodel, axiom, left, right);
                    });

                } else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                    Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
                    subClassOfAxioms = ((OWLDisjointClassesAxiom) axiom).asOWLSubClassOfAxioms();

                    subClassOfAxioms.forEach(ax -> {
                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
                        
                            this.patternify(this.metamodel, axiom, left, right);
                    });

                }
            } catch (Exception e) {
                if (!(e instanceof EmptyStackException))
                    System.out.println("Exception during translation: " + e.toString() + " at "
                            + e.getStackTrace()[0].getFileName() + " (" + e.getStackTrace()[0].getLineNumber() + ")");
                this.unsupported.addAxiom(axiom);
                this.metrics.put("unsupportedAxiomsCount", ((int) this.metrics.get("unsupportedAxiomsCount")) + 1);
            }
        });

        // dealing with the delayed forall axioms
        this.forallax.forEach(axf -> {
            try {
                if (axf.isOfType(AxiomType.SUBCLASS_OF)) {
                    OWLClassExpression left = ((OWLSubClassOfAxiom) axf).getSubClass();
                    OWLClassExpression right = ((OWLSubClassOfAxiom) axf).getSuperClass();

                    OWLObjectPropertyExpression property = ((OWLObjectAllValuesFrom) right).getProperty();
                    OWLClassExpression exists = new OWLObjectSomeValuesFromImpl(property,
                            new OWLClassImpl(IRI.create("http://www.w3.org/2002/07/owl#Thing")));

                    System.out.println(exists);
                    if (this.objpe.contains(exists)) {
                        System.out.println("It is entailed exists property");
                        Ax3 ax3asKF = new Ax3();
                        ax3asKF.type3ImportedAsKF(this.metamodel, left, right);
                    } else {
                        throw new EmptyStackException();
                    }
                }
                this.supported.addAxioms(axf);
                this.metrics.put("supportedAxiomsCount", ((int) this.metrics.get("supportedAxiomsCount")) + 1);
            } catch (Exception e) {
                if (!(e instanceof EmptyStackException))
                    System.out.println("Exception during translation: " + e.toString() + " at "
                            + e.getStackTrace()[0].getFileName() + " (" + e.getStackTrace()[0].getLineNumber() + ")");
                this.unsupported.addAxiom(axf);
                this.metrics.put("unsupportedAxiomsCount", ((int) this.metrics.get("unsupportedAxiomsCount")) + 1);
            }
        });

        end = Calendar.getInstance().getTimeInMillis();
        this.metrics.put("translationTime", (double) ((double) end - (double) start) / 1000.0);
    }

    /**
     * Iterates unsupported axioms and add them to a JSON.
     *
     * @return JSONObject with unsupported axioms.
     */
    public JSONObject getUnsupportedAxioms() {
        JSONObject axioms = new JSONObject();
        int[] index = { 1 };

        this.unsupported.tboxAxioms(Imports.EXCLUDED).forEachOrdered(axiom -> {
            axioms.put("axiom" + index[0], axiom.toString());
            index[0]++;
        });
        return axioms;
    }

    /**
     * Iterates supported axioms and add them to a JSON.
     *
     * @return JSONObject with unsupported axioms.
     */
    public JSONObject getSupportedAxioms() {
        JSONObject axioms = new JSONObject();
        int[] index = { 1 };

        this.supported.tboxAxioms(Imports.EXCLUDED).forEachOrdered(axiom -> {
            axioms.put("axiom" + index[0], axiom.toString());
            index[0]++;
        });
        return axioms;
    }

    /**
     * Export KF metamodel result from importation.
     *
     * @return JSONObject with the result of translation: metamodel, metrics and
     *         unsupported axioms.
     */
    public JSONObject toJSON() {
        JSONObject values = new JSONObject();

        values.put("kf", this.converter.generateJSON(metamodel));

        values.put("metrics", this.metrics);

        values.put("supported", this.getSupportedAxioms());

        values.put("unsupported", this.getUnsupportedAxioms());

        return values;
    }
}
