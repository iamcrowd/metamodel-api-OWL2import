package com.gilia.owlimporter.importer;

import java.util.Calendar;

import static com.gilia.utils.ImportUtils.validateOWL;

import com.gilia.builder.metabuilder.*;
import com.gilia.exceptions.EmptyOntologyException;
import com.gilia.metamodel.*;
import com.gilia.utils.Constants;
import static com.gilia.utils.Utils.getAlphaNumericString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.simple.JSONObject;
import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.*;
import org.springframework.web.multipart.MultipartFile;
import www.ontologyutils.normalization.Normalization;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;

import static com.gilia.utils.Constants.URI_NORMAL_CONCEPT;
import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;

/**
 * An importer is a KF metamodel instance of an OWL 2 specification
 *
 * @author gbraun
 *
 */
public class Importer {

    private Metamodel kfimported;
    private MetaConverter metabuilder;
    private OWLOntology onto;
    private OWLOntologyManager man;
    private OWLOntology naive;
    private OWLOntology unsupported;

    // Metrics
    long nOfLogAxioms;
    long nOfEntities;
    long nOfNormAxioms;
    long nOfNormEntities;
    long nOfLogUnsupportedAxioms;
    long nOfFresh;
    long nOfImport;
    double importingTime;

    /**
     *
     * @param filePath, a String containing an Ontology URI
     * @param precompute, true if you want reasoning over ontology before
     * importing. Otherwise, false.
     */
    public Importer(String filePath, Boolean precompute) {
        try {
            File file = new File(filePath);
            validateOWL(file);
            this.kfimported = new Metamodel();
            this.man = OWLManager.createOWLOntologyManager();
            this.onto = man.loadOntologyFromOntologyDocument(file);

            if (precompute) {
                ReasonerFactory factory = new ReasonerFactory();
                OWLReasoner reasoner = factory.createReasoner(this.onto);
                reasoner.precomputeInferences(
                        InferenceType.CLASS_HIERARCHY,
                        InferenceType.CLASS_ASSERTIONS,
                        InferenceType.DISJOINT_CLASSES,
                        InferenceType.OBJECT_PROPERTY_HIERARCHY,
                        InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_HIERARCHY
                );
                this.onto = reasoner.getRootOntology();
            }

            this.metabuilder = new MetaConverter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param multipartFile, a MultiparFile from a FormData containing an
     * Ontology
     * @param precompute, true if you want reasoning over ontology before
     * importing. Otherwise, false.
     */
    public Importer(MultipartFile multipartFile, Boolean precompute) {
        try {
            File file = new File("src/main/resources/temporalOWL.tmp");
            try ( OutputStream os = new FileOutputStream(file)) {
                os.write(multipartFile.getBytes());
            }
            validateOWL(file);
            this.kfimported = new Metamodel();
            this.man = OWLManager.createOWLOntologyManager();
            this.onto = man.loadOntologyFromOntologyDocument(file);

            if (precompute) {
                ReasonerFactory factory = new ReasonerFactory();
                OWLReasoner reasoner = factory.createReasoner(this.onto);
                reasoner.precomputeInferences(
                        InferenceType.CLASS_HIERARCHY,
                        InferenceType.CLASS_ASSERTIONS,
                        InferenceType.DISJOINT_CLASSES,
                        InferenceType.OBJECT_PROPERTY_HIERARCHY,
                        InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_HIERARCHY
                );
                this.onto = reasoner.getRootOntology();
            }

            this.metabuilder = new MetaConverter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param iri, a String containing an Ontology URI
     * @param precompute, true if you want reasoning over ontology before
     * importing. Otherwise, false.
     */
    public Importer(IRI iri, Boolean precompute) {
        try {
            validateOWL(iri);
            this.kfimported = new Metamodel();
            this.man = OWLManager.createOWLOntologyManager();
            this.onto = man.loadOntologyFromOntologyDocument(iri);

            if (precompute) {
                ReasonerFactory factory = new ReasonerFactory();
                OWLReasoner reasoner = factory.createReasoner(this.onto);
                reasoner.precomputeInferences(
                        InferenceType.CLASS_HIERARCHY,
                        InferenceType.CLASS_ASSERTIONS,
                        InferenceType.DISJOINT_CLASSES,
                        InferenceType.OBJECT_PROPERTY_HIERARCHY,
                        InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_ASSERTIONS,
                        InferenceType.DATA_PROPERTY_HIERARCHY
                );
                this.onto = reasoner.getRootOntology();
            }

            this.metabuilder = new MetaConverter();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Starting to loop over ontology axioms to normalise Filter axiom types
     * http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
     *
     * @implNote we remove unsupported class expressions not removed by ontology
     * utils dependency
     *
     * @param ontology
     * @return a normalised ontology to be imported
     * @deprecated
     */
    public OWLOntology normalizeToImport(OWLOntology ontology) {
        FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

        OWLOntology copy = Utils.newEmptyOntology();
        copy.addAxioms(ontology.axioms());

        OWLOntology unsupported = Utils.newEmptyOntology();

        Stream<OWLAxiom> tBoxAxioms = copy.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach(
                ax -> {
                    copy.remove(ax);

                    try {
                        OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
                        OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();

                        if ((left.getClassExpressionType()
                        == ClassExpressionType.OBJECT_HAS_VALUE)
                        || (right.getClassExpressionType()
                        == ClassExpressionType.OBJECT_HAS_VALUE)) {
                            unsupported.addAxiom(ax);
                        } else {
                            copy.addAxioms(NormalizationTools.asSubClassOfAxioms(ax));
                        }
                    } catch (Exception f) {
                        System.out.println("Unsupported axioms:" + ax.toString());
                    }
                }
        );

        System.out.println("\nNaive Normalized TBox");
        //OWLOntology naive = null;
        OWLOntology naive = Utils.newEmptyOntology();
        naive = Normalization.normalizeNaive(copy);

        System.out.println(
                "\n ************List Unsupported ClassExpressions and Axioms in Normalization App\n"
        );
        // After normalize, copy again the unsupported axioms

        unsupported.axioms().forEach(System.out::println);

        naive.addAxioms(unsupported.axioms());

        // check every axiom of the original ontology is entailed in naive
        OWLReasoner reasoner = Utils.getHermitReasoner(naive);
        //assert (ontology.axioms().allMatch(ax -> reasoner.isEntailed(ax)));

        copy.addAxioms(unsupported.axioms());

        // check every axiom of naive is entailed in the copy of the original ontology
        // with extended signature
        copy.addAxioms(FreshAtoms.getFreshAtomsEquivalenceAxioms());
        OWLReasoner reasonerBis = Utils.getHermitReasoner(copy);
        assert (naive.axioms().allMatch(ax -> reasonerBis.isEntailed(ax)));

        naive
                .tboxAxioms(Imports.EXCLUDED)
                .forEach(ax -> System.out.println(Utils.pretty("-- " + ax.toString())));

        return naive;
    }

    public Metamodel getKFInstance() {
        return this.kfimported;
    }

    public OWLOntologyManager getOntologyManager() {
        return this.man;
    }

    public OWLOntology getOntology() {
        return this.onto;
    }

    public OWLOntology getNaive() {
        return this.naive;
    }

    public OWLOntology getUnsupportedAxioms() {
        return this.unsupported;
    }

    public JSONObject showOntology() {
        return this.showOntology(this.onto);
    }

    public JSONObject showOntology(OWLOntology ontology) {
        JSONObject jsonAx = new JSONObject();
        int[] index = {1};

        ontology.tboxAxioms(Imports.EXCLUDED).forEachOrdered((ax) -> {
            jsonAx.put("axiom" + index[0], ax.toString());
            index[0]++;
        });
        return jsonAx;
    }

    public JSONObject showUnsupported() {
        JSONObject jsonAx = new JSONObject();
        int[] index = {1};

        this.unsupported.tboxAxioms(Imports.EXCLUDED).forEachOrdered((ax) -> {
            jsonAx.put("axiom" + index[0], ax.toString());
            index[0]++;
        });
        return jsonAx;
    }

    /**
     * Metamodel instance is to exported as a JSONObject according to the KF
     * JSON Scheme Scheme
     *
     * @return JSONObject KF metamodel
     */
    public JSONObject toJSON() {
        return this.metabuilder.generateJSON(this.kfimported);
    }

    public JSONObject toJSONMetrics() {
        JSONObject metrics = new JSONObject();

        this.calculateMetrics();

        metrics.put("nOfLogAxioms", this.nOfLogAxioms);
        metrics.put("nOfEntities", this.nOfEntities);
        metrics.put("nOfNormAxioms", this.nOfNormAxioms);
        metrics.put("nOfNormEntities", this.nOfNormEntities);
        metrics.put("nOfLogUnsupportedAxioms", this.nOfLogUnsupportedAxioms);
        metrics.put("nOfFresh", this.nOfFresh);
        metrics.put("nOfImport", this.nOfImport);
        metrics.put("importingTime", this.importingTime);

        JSONObject values = new JSONObject();
        values.put("metrics", metrics);
        values.put("kf", this.toJSON());
        values.put("unsupported", this.showUnsupported());

        return values;
    }

    /**
     * Normalise full ontology
     */
    public void importNormalisedOntology() {
        long init, fin;
        init = Calendar.getInstance().getTimeInMillis();

        NormalFormTools tools = new NormalFormTools();
        tools.asKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);

        fin = Calendar.getInstance().getTimeInMillis();
        this.importingTime = (double) ((double) fin - (double) init) / 1000.0;

        this.naive = tools.getNaive();
        this.unsupported = tools.getUnsupportedAxioms();
    }

    /**
     * Partial normalisation
     */
    public void importType1AfromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type1ANormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType1BfromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type1BNormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType1CfromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type1CNormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType1DfromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type1DNormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType2fromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type2NormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType3fromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type3NormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    public void importType4fromOntology() {
        NormalFormTools tools = new NormalFormTools();
        tools.type4NormalisedasKF(this.kfimported, this.onto);
        MetaBuilder builder = new MetaConverter();
        builder.generateJSON(this.kfimported);
    }

    /**
     * get Metrics
     *
     */
    public long getNumberOfAx() {
        return this.nOfLogAxioms;
    }

    public long getNumberOfEntities() {
        return this.nOfEntities;
    }

    public long getNumberOfNormAx() {
        return this.nOfNormAxioms;
    }

    public long getNumberOfNormEntities() {
        return this.nOfNormEntities;
    }

    public long getNumberOfNonNormAx() {
        return this.nOfLogUnsupportedAxioms;
    }

    public double getImportingTime() {
        return this.importingTime;
    }

    /**
     * Calculate metrics
     */
    public void calculateMetrics() {
        this.numberOfAx();
        this.numberOfEntities();
        this.numberOfAxInNormalised();
        this.numberOfEntitiesInNormalised();
        this.numberOfNonNormAx();
        this.numberOfFresh();
        this.numberOfImport();
    }

    /**
     * Metric calculations
     *
     * Number of axioms in original ontology. Only logical axioms.
     *
     * @return number of logical axioms.
     */
    public void numberOfAx() {
        Stream<OWLAxiom> tBoxAxioms = this.onto.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach((ax) -> {
            if (ax.isLogicalAxiom()) {
                this.nOfLogAxioms++;
            }
        });
    }

    /**
     * Metrics
     *
     * Number of entities in the signature of the original ontology. The
     * signature is the set of entities used to build axioms and annotations.
     *
     * @return number of entities in the ontology signature.
     */
    public void numberOfEntities() {
        this.nOfEntities = this.onto.signature().count();
    }

    /**
     * Metrics
     *
     * Number of axioms in normalised ontology. Only AxiomType SUBCLASS_OF.
     *
     * @return number of SUBCLASS_OF normalised axioms
     */
    public void numberOfAxInNormalised() {
        this.nOfNormAxioms = this.naive.axioms(AxiomType.SUBCLASS_OF).count();
    }

    /**
     * Metrics
     *
     * Number of entities in normalised ontology. The signature is the set of
     * entities used to build axioms and annotations.
     */
    public void numberOfEntitiesInNormalised() {
        this.nOfNormEntities = this.naive.signature().count();
    }

    /**
     * Metrics
     *
     * Number of non-normalised axioms in original ontology. Only logical
     * axioms.
     *
     * @return number of logical axioms.
     */
    public void numberOfNonNormAx() {
        Stream<OWLAxiom> tBoxAxioms = this.unsupported.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach((ax) -> {
            if (ax.isLogicalAxiom()) {
                this.nOfLogUnsupportedAxioms++;
            }
        });
    }

    /**
     * Metrics
     *
     * Number of fresh generated concepts.
     *
     * @return number of fresh concepts.
     */
    public void numberOfFresh() {
        kfimported.getEntities().forEach((entity) -> {
            if (entity.getName().startsWith(URI_NORMAL_CONCEPT)) {
                this.nOfFresh++;
            }
        });
    }

    /**
     * Metrics
     *
     * Number of import generated concepts by KF translate.
     *
     * @return number of import KF concepts.
     */
    public void numberOfImport() {
        kfimported.getEntities().forEach((entity) -> {
            if (entity.getName().startsWith(URI_IMPORT_CONCEPT)) {
                this.nOfImport++;
            }
        });
    }
}
