package com.gilia.owlimport;

import static com.gilia.utils.Constants.TYPE2_SUBCLASS_AXIOM;
import static com.gilia.utils.ImportUtils.*;
import static com.gilia.utils.Utils.*;

import com.gilia.builder.metabuilder.*;
import com.gilia.metamodel.*;
import com.gilia.owlimport.axtoKF.*;
import com.gilia.utils.Constants;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.*;

import org.json.simple.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;

import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasonerFactory;
import org.semanticweb.owlapi.owllink.OWLlinkReasonerConfigurationImpl;

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
    private OWLReasoner reasoner;
    private String reasonerName;
    private boolean reasoning = false;
    private boolean filtering = true;

    private List<InferredAxiomGenerator<? extends OWLAxiom>> axiomsGens;

    private List<OWLClassExpression> objpe = new ArrayList<>();
    private List<OWLAxiom> forallax = new ArrayList<>();

    private OWLImporterMetrics metrics;

    public OWLImporter() {
        this.manager = OWLManager.createOWLOntologyManager();
        this.metrics = new OWLImporterMetrics();
        this.reset();
    }

    /**
     * Reset resultant metamodel, supported and unsupported axioms for load a new
     * ontology.
     */
    public void reset() {
        this.metamodel = new Metamodel();
        this.supported = Utils.newEmptyOntology();
        this.unsupported = Utils.newEmptyOntology();
        this.metrics.reset();
    }

    /**
     *
     * @param iri a String containing an Ontology URI.
     */
    public void load(IRI iri) throws Exception {
        try {
            this.reset();
            validateOWL(iri);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(iri);
            if (this.ontology.isEmpty()) {
                throw new IllegalArgumentException("Ontology is empty.");
            }
        } catch (Exception e) {
            printException("Exception during ontology loading (load), with IRI: " + iri, e);
            throw new Exception("Exception during ontology loading.", e);
        }
    }

    /**
     *
     * @param string a String containing an Ontology RDF/XML file.
     */
    public void load(String string) throws Exception {
        try {
            this.reset();
            this.ontology = this.manager
                    .loadOntologyFromOntologyDocument(new ByteArrayInputStream(string.getBytes("UTF-8")));
            if (this.ontology.isEmpty()) {
                throw new IllegalArgumentException("Ontology is empty.");
            }
        } catch (Exception e) {
            printException("Exception during ontology loading (load), with String: " + string, e);
            throw new Exception("Exception during ontology loading.", e);
        }
    }

    /**
     *
     * @param path a String containing a file path to an Ontology File.
     */
    public void loadFromPath(String path) throws Exception {
        try {
            this.reset();
            File file = new File(path);
            validateOWL(file);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
            if (this.ontology.isEmpty()) {
                throw new IllegalArgumentException("Ontology is empty.");
            }
        } catch (Exception e) {
            printException("Exception during ontology loading (load), with Path: " + path, e);
            throw new Exception("Exception during ontology loading.", e);
        }
    }

    /**
     *
     * @param multipartFile a MultiparFile from a FormData containing an Ontology
     *                      File.
     */
    public void load(MultipartFile multipartFile) throws Exception {
        try {
            this.reset();
            File file = new File("src/main/resources/temporalOWL.tmp");
            try (OutputStream os = new FileOutputStream(file)) {
                os.write(multipartFile.getBytes());
            }
            validateOWL(file);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
            if (this.ontology.isEmpty()) {
                throw new IllegalArgumentException("Ontology is empty.");
            }
        } catch (Exception e) {
            printException("Exception during ontology loading (load), with MultipartFile", e);
            throw new Exception("Exception during ontology loading.", e);
        }
    }

    public void setFiltering(boolean filtering) {
        this.filtering = filtering;
    }

    /**
     * Loads the reasoner to be used by precompute method later.
     * 
     * @param reasonerName a String containing the name of the reasoner to be
     *                     loaded.
     */
    public void loadReasoner(String reasonerName) throws Exception {
        try {
            this.reasonerName = reasonerName;

            this.axiomsGens = new ArrayList<>();
            this.axiomsGens.add(new InferredSubClassAxiomGenerator());
            this.axiomsGens.add(new InferredClassAssertionAxiomGenerator());
            this.axiomsGens.add(new InferredDisjointClassesAxiomGenerator());
            this.axiomsGens.add(new InferredEquivalentClassAxiomGenerator());
            this.axiomsGens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
            this.axiomsGens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            this.axiomsGens.add(new InferredInverseObjectPropertiesAxiomGenerator());
            this.axiomsGens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
            this.axiomsGens.add(new InferredPropertyAssertionGenerator());
            this.axiomsGens.add(new InferredSubDataPropertyAxiomGenerator());
            this.axiomsGens.add(new InferredSubObjectPropertyAxiomGenerator());

            // reasoning is true when one reasoner is loaded
            this.reasoning = true;
            this.metrics.setReasoned(true);
            this.metrics.reset();
        } catch (Exception e) {
            printException("Exception during reasoner loading (loadReasoner)", e);
            throw new Exception("Exception during reasoner loading (" + reasonerName + ").", e);
        }
    }

    private URL setReasonerServer() throws Exception {
        URL url = null;
        try {
            url = new URL("http://localhost:8080");
        } catch (Exception e) {
            printException(
                    "Exception during precompute ontology, at reasoner server setting configuration (setReasonerServer) ("
                            + reasonerName + ")",
                    e);
            throw new Exception("Exception during precompute ontology, at reasoner server setting configuration ("
                    + reasonerName + ").", e);
        }
        return url;
    }

    /**
     * Executes a reasoner over the input ontology to get inferred axioms.
     */
    private void precompute() throws Exception {
        try {
            switch (this.reasonerName) {
                case Constants.JFACT:
                    OWLReasonerFactory reasonerFactoryFact = new JFactFactory();
                    this.reasoner = reasonerFactoryFact.createReasoner(this.ontology);
                    break;
                case Constants.RACER:
                    // Racer
                    // Run the tool as OWLlink server in the commandline. ./Racer -protocol OWLlink
                    OWLlinkHTTPXMLReasonerFactory reasonerFactoryRacer = new OWLlinkHTTPXMLReasonerFactory();
                    OWLlinkReasonerConfigurationImpl reasonerRacerConfiguration = new OWLlinkReasonerConfigurationImpl(
                            this.setReasonerServer());
                    this.reasoner = reasonerFactoryRacer.createReasoner(this.ontology, reasonerRacerConfiguration);
                    break;
                case Constants.KONCLUDE:
                    // Konclude
                    // Run the tool as OWLlink server in the commandline. ./Konclude owllinkserver
                    // -p 8080
                    OWLlinkHTTPXMLReasonerFactory reasonerFactoryKonclude = new OWLlinkHTTPXMLReasonerFactory();
                    OWLlinkReasonerConfigurationImpl reasonerKoncludeConfiguration = new OWLlinkReasonerConfigurationImpl(
                            this.setReasonerServer());
                    this.reasoner = reasonerFactoryKonclude.createReasoner(this.ontology,
                            reasonerKoncludeConfiguration);
                    break;
                case Constants.PELLET:
                default:
                    OWLReasonerFactory reasonerFactoryPellet = new OpenlletReasonerFactory();
                    this.reasoner = reasonerFactoryPellet.createReasoner(this.ontology);
                    break;
            }

            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, this.axiomsGens);
            OWLDataFactory df = OWLManager.getOWLDataFactory();
            iog.fillOntology(df, this.ontology);
        } catch (Exception e) {
            printException("Exception during precompute ontology (precompute) (" + reasonerName + ")", e);
            throw new Exception("Exception during precompute ontology (" + reasonerName + ").", e);
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
        if (OWLAxForm.isAtom(left) && OWLAxForm.isAtom(right)) {
            // atom -> atom
            Ax1A ax1A = new Ax1A();
            ax1A.type1AasKF(kf, left, right);
            this.metrics.add("axiomSubClassOfCount", "translation");
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isDisjunctionOfAtoms(right)) {
            // atom -> disjunction
            Ax1B ax1B = new Ax1B();
            ax1B.type1BasKF(kf, left, right);
            this.metrics.add("axiomUnionOfCount", "translation");
        } else if (OWLAxForm.isDisjunctionOfAtoms(left) && OWLAxForm.isAtom(right)) {
            // disjunction -> atom
            Collection<OWLClassExpression> disjunctionAtoms = ((OWLObjectUnionOf) left).getOperandsAsList();
            for (OWLClassExpression disjunctionAtom : disjunctionAtoms) {
                Ax1A ax1A = new Ax1A();
                ax1A.type1AasKF(kf, disjunctionAtom, right);
                this.metrics.add("axiomSubClassOfCount", "translation");
            }
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isConjunctionOfAtoms(right)) {
            // atom -> conjunction
            Collection<OWLClassExpression> conjunctionAtoms = ((OWLObjectIntersectionOf) right).getOperandsAsList();
            for (OWLClassExpression conjunctionAtom : conjunctionAtoms) {
                Ax1A ax1A = new Ax1A();
                ax1A.type1AasKF(kf, left, conjunctionAtom);
                this.metrics.add("axiomSubClassOfCount", "translation");
            }
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isComplementOfAtoms(right)) {
            // atom -> complementOf atom
            // removing \bottom -> complement_of top (\top -> complement_of \bottom)
            OWLClassExpression complement = ((OWLObjectComplementOf) right).getOperand();
            if (!(left.isOWLNothing() && complement.isOWLThing()) &&
                    !(complement.isOWLNothing() && left.isOWLThing())) {
                AxComplementOf axComp = new AxComplementOf();
                axComp.complementOfasKF(kf, left, right);
                this.metrics.add("axiomComplementOfCount", "translation");
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
                this.metrics.add("axiomExistsCount", "translation");
            } else {
                throw new EmptyStackException();
            }
        } else if (OWLAxForm.isExistentialOfAtom(right) && OWLAxForm.isAtom(left)) {
            // exists property atom -> ... this pattern only collects the exists axioms of
            // the ontology
            OWLObjectPropertyExpression property = ((OWLObjectSomeValuesFrom) right).getProperty();
            OWLObjectProperty namedProperty = property.getNamedProperty();

            if (property.isNamed()) {
                this.objpe.add(right);
                this.supported.removeAxiom(axiom);
                this.metrics.remove("supportedAxiomsCount", "translation");
            } else {
                throw new EmptyStackException();
            }
        } else if (OWLAxForm.isAtom(left) && OWLAxForm.isUniversalOfAtom(right)) {
            // atom -> forall property atom
            OWLObjectPropertyExpression property = ((OWLObjectAllValuesFrom) right).getProperty();
            OWLObjectProperty namedProperty = property.getNamedProperty();

            if (property.isNamed()) {
                this.forallax.add(axiom);
                this.supported.removeAxiom(axiom);
                this.metrics.remove("supportedAxiomsCount", "translation");
            } else {
                throw new EmptyStackException();
            }
        } else {
            throw new EmptyStackException();
        }
    }

    /**
     * Return true if the pass the filters.
     * 
     * Filtered axioms:
     * subclass(atom, top)
     * subclass(bottom, atom)
     * disjoint(bottom, atom)
     * 
     */
    private boolean filter(OWLAxiom axiom) throws Exception {
        try {
            boolean passFilter = true;

            if (filtering) {
                if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
                    OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();

                    if ((right.isTopEntity() && OWLAxForm.isAtom(left))
                            || (left.isBottomEntity() && OWLAxForm.isAtom(right))) {
                        passFilter = false;
                    }
                } else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                    OWLClassExpression left = ((OWLDisjointClassesAxiom) axiom).getOperandsAsList().get(0);
                    OWLClassExpression right = ((OWLDisjointClassesAxiom) axiom).getOperandsAsList().get(1);

                    if ((left.isBottomEntity() && OWLAxForm.isAtom(right))
                            || (right.isBottomEntity() && OWLAxForm.isAtom(left))) {
                        passFilter = false;
                    }
                }
            }

            return passFilter;
        } catch (Exception e) {
            System.out.println("Exception filtering axiom. (" + e.toString() + ")");
            throw e;
        }
    }

    /**
     * Translation to KF metamodel. It does reasoning first and then translate each
     * supported axiom and register both: supported and unsupported axioms.
     *
     * @todo objpe should be a set for avoiding repretitions.
     * 
     * @implNote still missing:
     *           union -> atom could be rewritten as atomic subclasses,
     *           disjoint union (c, c1, ... cn) as equivalent(c, union_of(c1,..,cn))
     *           and disjoint(c1,...,cn)
     *           min, max, exact cardinalities
     */
    public void translate() throws Exception {
        this.metrics.startTimer("translationTime", "translation");

        this.metrics.calculateOntologyMetrics(this.ontology, false);

        if (this.reasoning) {
            // reason over the input ontology
            this.precompute();

            this.metrics.calculateOntologyMetrics(this.ontology, true);
        }

        // get all tbox axioms
        Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        System.out.println("Axioms: ");

        // iterate each axiom
        for (OWLAxiom axiom : tboxAxioms) {
            try {
                // check if pass the filters
                if (this.filter(axiom)) {
                    System.out.println("    " + axiom.toString());

                    // determine if axiom is of a supported type
                    if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                        this._translateSubclassOf(axiom);
                        this.supported.addAxioms(axiom);
                        this.metrics.add("supportedAxiomsCount", "translation");
                    } else if (axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
                        this._translateEquivalentClasses(axiom);
                        this.supported.addAxioms(axiom);
                        this.metrics.add("supportedAxiomsCount", "translation");
                    } else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                        this._translateDisjointClasses(axiom);
                        this.supported.addAxioms(axiom);
                        this.metrics.add("supportedAxiomsCount", "translation");
                    } else if (axiom.isOfType(AxiomType.DISJOINT_UNION)) {
                        this._translateDisjointClasses(
                                ((OWLDisjointUnionAxiom) axiom).getOWLDisjointClassesAxiom());
                        this._translateEquivalentClasses(
                                ((OWLDisjointUnionAxiom) axiom).getOWLEquivalentClassesAxiom());
                        this.supported.addAxioms(axiom);
                        this.metrics.add("supportedAxiomsCount", "translation");
                    } else {
                        this.unsupported.addAxiom(axiom);
                        this.metrics.add("unsupportedAxiomsCount", "translation");
                    }
                } else {
                    System.out.println("    (filtered) " + axiom.toString());
                    this.metrics.add("filteredAxiomsCount", "translation");
                }
            } catch (Exception e) {
                if (!(e instanceof EmptyStackException))
                    System.out.println("Exception during translation: " + e.toString() + " at "
                            + e.getStackTrace()[0].getFileName() + " (" + e.getStackTrace()[0].getLineNumber() + ")");
                this.unsupported.addAxiom(axiom);
                this.metrics.add("unsupportedAxiomsCount", "translation");
            }
        }

        // dealing with the delayed forall axioms
        for (OWLAxiom axf : this.forallax) {
            try {
                if (axf.isOfType(AxiomType.SUBCLASS_OF)) {
                    OWLClassExpression left = ((OWLSubClassOfAxiom) axf).getSubClass();
                    OWLClassExpression right = ((OWLSubClassOfAxiom) axf).getSuperClass();

                    OWLObjectPropertyExpression property = ((OWLObjectAllValuesFrom) right).getProperty();
                    OWLClassExpression exists = new OWLObjectSomeValuesFromImpl(property,
                            new OWLClassImpl(IRI.create("http://www.w3.org/2002/07/owl#Thing")));

                    System.out.println("    " + exists);
                    if (this.objpe.contains(exists)) {
                        System.out.println("    (It is entailed exists property)");
                        Ax3 ax3asKF = new Ax3();
                        ax3asKF.type3ImportedAsKF(this.metamodel, left, right);
                        this.metrics.add("axiomForAllCount", "translation");
                        this.supported.addAxioms(axf);
                        this.metrics.add("supportedAxiomsCount", "translation");
                    } else {
                        this.unsupported.addAxiom(axf);
                        this.metrics.add("unsupportedAxiomsCount", "translation");
                    }
                }

            } catch (Exception e) {
                if (!(e instanceof EmptyStackException))
                    System.out.println("Exception during translation: " + e.toString() + " at "
                            + e.getStackTrace()[0].getFileName() + " (" + e.getStackTrace()[0].getLineNumber() + ")");
                this.unsupported.addAxiom(axf);
                this.metrics.add("unsupportedAxiomsCount", "translation");
            }
        }

        this.metrics.stopTimer("translationTime", "translation");
    }

    private void _translateSubclassOf(OWLAxiom axiom) {
        // get left and right expressions (SubClass -> SuperClass)
        OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
        OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();

        this.patternify(this.metamodel, axiom, left, right);
    }

    private void _translateEquivalentClasses(OWLAxiom axiom) {
        Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
        subClassOfAxioms = ((OWLEquivalentClassesAxiom) axiom).asOWLSubClassOfAxioms();

        subClassOfAxioms.forEach(ax -> {
            OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
            OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();

            this.patternify(this.metamodel, axiom, left, right);
        });
    }

    private void _translateDisjointClasses(OWLAxiom axiom) {
        Collection<OWLSubClassOfAxiom> subClassOfAxioms = new ArrayList<OWLSubClassOfAxiom>();
        subClassOfAxioms = ((OWLDisjointClassesAxiom) axiom).asOWLSubClassOfAxioms();

        List<String> leftTracked = new ArrayList<String>();
        subClassOfAxioms.forEach(ax -> {
            OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
            OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();

            leftTracked.add(left.toString());
            if (!leftTracked.contains(((OWLObjectComplementOf) right).getOperand().toString())) {
                this.patternify(this.metamodel, axiom, left, right);
            }
        });
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
    public JSONObject toJSON() throws Exception {
        try {
            JSONObject values = new JSONObject();

            // coverter must be re-initialized each time because metrics are not reset for
            // each JSON generation
            this.converter = new MetaConverter();
            values.put("kf", this.converter.generateJSON(metamodel));
            this.metrics.calculateKFMetrics(this.converter, this.metamodel);

            values.put("metrics", this.metrics.get());

            values.put("supported", this.getSupportedAxioms());

            values.put("unsupported", this.getUnsupportedAxioms());

            return values;
        } catch (Exception e) {
            printException("Exception during ontology serialization (toJSON)", e);
            throw new Exception("Exception during ontology serialization.", e);
        }
    }
}
