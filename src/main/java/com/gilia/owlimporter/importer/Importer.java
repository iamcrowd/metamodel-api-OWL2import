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
import org.semanticweb.owlapi.metrics.DLExpressivity;
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
    String expressivity;
    long nOfLogAxioms;
    long nOfEntities;
    long nOfNormAxioms;
    long nOfNormEntities;
    long nOfLogUnsupportedAxioms;
    long nOfFresh;
    long nOfImport;
    double importingTime;
    long nOfClassesInOrig;
    long nOfObjectPropertiesInOrig;
    long nOfDataPropertiesInOrig;
    long nOfClassesInNormalised;
    long nOfObjectPropertiesInNormalised;
    long nOfDataPropertiesInNormalised;
    long nOfAx1A;
    long nOfAx1B;
    long nOfAx1C;
    long nOfAx1D;
    long nOfAx2A;
    long nOfAx2AInv;
    long nOfAx2B;
    long nOfAx2BInv;
    long nOfAx2C;
    long nOfAx2CInv;
    long nOfAx2D;
    long nOfAx2DInv;
    long nOfAx3;
    long nOfAx3Inv;
    long nOfAx4;
    long nOfAx4Inv;

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
            this.metabuilder = new MetaConverter();

            this.precomputeOntology(precompute);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
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
            this.metabuilder = new MetaConverter();

            this.precomputeOntology(precompute);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
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
            this.metabuilder = new MetaConverter();

            this.precomputeOntology(precompute);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }

    private void precomputeOntology(Boolean precompute) {
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
                        //System.out.println("Unsupported axioms:" + ax.toString());
                    }
                }
        );

        //System.out.println("\nNaive Normalized TBox");
        //OWLOntology naive = null;
        OWLOntology naive = Utils.newEmptyOntology();
        naive = Normalization.normalizeNaive(copy);

        //System.out.println(
        //        "\n ************List Unsupported ClassExpressions and Axioms in Normalization App\n"
        //);
        // After normalize, copy again the unsupported axioms
        //unsupported.axioms().forEach(System.out::println);
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

        //naive
        //        .tboxAxioms(Imports.EXCLUDED)
        //        .forEach(ax -> System.out.println(Utils.pretty("-- " + ax.toString())));
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

        metrics.put("expressivity", this.expressivity);
        metrics.put("nOfLogAxioms", this.nOfLogAxioms);
        metrics.put("nOfEntities", this.nOfEntities);
        metrics.put("nOfNormAxioms", this.nOfNormAxioms);
        metrics.put("nOfNormEntities", this.nOfNormEntities);
        metrics.put("nOfLogUnsupportedAxioms", this.nOfLogUnsupportedAxioms);
        metrics.put("nOfFresh", this.nOfFresh);
        metrics.put("nOfImport", this.nOfImport);
        metrics.put("importingTime", this.importingTime);
        metrics.put("nOfClassesInOrig", this.nOfClassesInOrig);
        metrics.put("nOfObjectPropertiesInOrig", this.nOfObjectPropertiesInOrig);
        metrics.put("nOfDataPropertiesInOrig", this.nOfDataPropertiesInOrig);
        metrics.put("nOfClassesInNormalised", this.nOfClassesInNormalised);
        metrics.put("nOfObjectPropertiesInNormalised", this.nOfObjectPropertiesInNormalised);
        metrics.put("nOfDataPropertiesInNormalised", this.nOfDataPropertiesInNormalised);
        metrics.put("nOfAx1A", this.nOfAx1A);
        metrics.put("nOfAx1B", this.nOfAx1B);
        metrics.put("nOfAx1C", this.nOfAx1C);
        metrics.put("nOfAx1D", this.nOfAx1D);
        metrics.put("nOfAx2A", this.nOfAx2A);
        metrics.put("nOfAx2AInv", this.nOfAx2AInv);
        metrics.put("nOfAx2B", this.nOfAx2B);
        metrics.put("nOfAx2BInv", this.nOfAx2BInv);
        metrics.put("nOfAx2C", this.nOfAx2C);
        metrics.put("nOfAx2CInv", this.nOfAx2CInv);
        metrics.put("nOfAx2D", this.nOfAx2D);
        metrics.put("nOfAx2DInv", this.nOfAx2DInv);
        metrics.put("nOfAx3", this.nOfAx3);
        metrics.put("nOfAx3Inv", this.nOfAx3Inv);
        metrics.put("nOfAx4", this.nOfAx4);
        metrics.put("nOfAx4Inv", this.nOfAx4Inv);

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
        //MetaBuilder builder = new MetaConverter();
        //builder.generateJSON(this.kfimported);
        this.metabuilder.generateJSON(this.kfimported);

        fin = Calendar.getInstance().getTimeInMillis();
        this.importingTime = (double) ((double) fin - (double) init) / 1000.0;

        this.naive = tools.getNaive();
        this.unsupported = tools.getUnsupportedAxioms();
        this.nOfAx1A = tools.getnOfAx1A();
        this.nOfAx1B = tools.getnOfAx1B();
        this.nOfAx1C = tools.getnOfAx1C();
        this.nOfAx1D = tools.getnOfAx1D();
        this.nOfAx2A = tools.getnOfAx2A();
        this.nOfAx2AInv = tools.getnOfAx2AInv();
        this.nOfAx2B = tools.getnOfAx2B();
        this.nOfAx2BInv = tools.getnOfAx2BInv();
        this.nOfAx2C = tools.getnOfAx2C();
        this.nOfAx2CInv = tools.getnOfAx2CInv();
        this.nOfAx2D = tools.getnOfAx2D();
        this.nOfAx2DInv = tools.getnOfAx2DInv();
        this.nOfAx3 = tools.getnOfAx3();
        this.nOfAx3Inv = tools.getnOfAx3Inv();
        this.nOfAx4 = tools.getnOfAx4();
        this.nOfAx4Inv = tools.getnOfAx4Inv();
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
    public String getExpressivity() {
        return expressivity;
    }

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

    public long getNumberOfFresh() {
        return this.nOfFresh;
    }

    public long getNumberOfImport() {
        return this.nOfImport;
    }

    public long getNumberOfClassesInOrig() {
        return this.nOfClassesInOrig;
    }

    public long getNumberOfClassesInNorm() {
        return this.nOfClassesInNormalised;
    }

    /**
     * Get Metrics KF
     *
     */
    public long getNofEntities() {
        return this.metabuilder.getNofEntities();
    }

    public long getNofObjectTypes() {
        return this.metabuilder.getNofObjectTypes();
    }

    public long getNofAttributes() {
        return this.metabuilder.getNofAttributes();
    }

    public long getNofSubsumptions() {
        return this.metabuilder.getNofSubsumptions();
    }

    public long getNofRoles() {
        return this.metabuilder.getNofRoles();
    }

    public long getNofBinaryRels() {
        return this.metabuilder.getNofBinaryRels();
    }

    public long getNofRels() {
        return this.metabuilder.getNofRels();
    }

    public long getNofDataTypes() {
        return this.metabuilder.getNofDataTypes();
    }

    public long getNofValueTypes() {
        return this.metabuilder.getNofValueTypes();
    }

    public long getNofAttributeProperties() {
        return this.metabuilder.getNofAttributeProperties();
    }

    public long getNofMappedTo() {
        return this.metabuilder.getNofMappedTo();
    }

    public long getNofCardinalities() {
        return this.metabuilder.getNofCardinalities();
    }

    public long getNofDisjointC() {
        return this.metabuilder.getNofDisjointC();
    }

    public long getNofCompletenessC() {
        return this.metabuilder.getNofCompletenessC();
    }

    public long getNofMandatory() {
        return this.metabuilder.getNofMandatory();
    }

    /**
     * Calculate metrics
     */
    public void calculateMetrics() {
        this.calculateExpressivity();
        this.numberOfAx();
        this.numberOfEntities();
        this.numberOfAxInNormalised();
        this.numberOfEntitiesInNormalised();
        this.numberOfNonNormAx();
        this.numberOfFresh();
        this.numberOfImport();
        this.numberOfClassesInOrig();
        this.numberOfPropertiesInOrig();
        this.numberOfClassesInNormalised();
        this.numberOfPropertiesInNormalised();
    }

    /**
     * Metric calculations
     *
     * Expressivity of ontologie
     *
     */
    public void calculateExpressivity() {
        try {
            DLExpressivity dlExpressivity = new DLExpressivity(this.onto);
            String expr = dlExpressivity.getValue();
            this.expressivity = expr.length() >= 1 ? expr : "unsupported";
        } catch (Exception e) {
            this.expressivity = "unsupported";
            System.out.println(e.getStackTrace().toString());
        }
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
                //System.out.println("\tEste es el axioma no soportado: " + ax.toString());
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

        this.kfimported.getEntities().forEach((entity) -> {
            //System.out.println("\tFresh COncept" + entity.getName());
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

    /**
     * Metric calculations
     *
     * Number of classes in original ontology.
     *
     * @return number of classes in original ontology.
     */
    public void numberOfClassesInOrig() {
        ArrayList<OWLClass> classes = new ArrayList<OWLClass>();
        this.onto.classesInSignature(Imports.EXCLUDED).forEach(classes::add);
        this.nOfClassesInOrig = classes.size();

        /*this.onto.classesInSignature(Imports.EXCLUDED).forEach((clase) -> {
        	System.out.println("Clase original" + clase);
        	}
        );*/
    }

    /**
     * Metric calculations
     *
     * Number of properties in original ontology.
     *
     * @return number of properties in original ontology.
     */
    public void numberOfPropertiesInOrig() {
        ArrayList<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
        this.onto.objectPropertiesInSignature(Imports.EXCLUDED).forEach(objectProperties::add);
        this.nOfObjectPropertiesInOrig = objectProperties.size();

        ArrayList<OWLDataProperty> dataProperties = new ArrayList<OWLDataProperty>();
        this.onto.dataPropertiesInSignature(Imports.EXCLUDED).forEach(dataProperties::add);
        this.nOfDataPropertiesInOrig = dataProperties.size();
    }

    /**
     * Metric calculations
     *
     * Number of classes in normalised ontology.
     *
     * @return number of classes in normalised ontology.
     */
    public void numberOfClassesInNormalised() {
        ArrayList<OWLClass> classes = new ArrayList<OWLClass>();
        this.naive.classesInSignature(Imports.EXCLUDED).forEach(classes::add);
        this.nOfClassesInNormalised = classes.size();

        /* this.naive.classesInSignature(Imports.EXCLUDED).forEach((clase) -> {
        	System.out.println("Clase normalised" + clase);
        	}
        );*/
    }

    /**
     * Metric calculations
     *
     * Number of properties in normalised ontology.
     *
     * @return number of properties in normalised ontology.
     */
    public void numberOfPropertiesInNormalised() {
        ArrayList<OWLObjectProperty> objectProperties = new ArrayList<OWLObjectProperty>();
        this.naive.objectPropertiesInSignature(Imports.EXCLUDED).forEach(objectProperties::add);
        this.nOfObjectPropertiesInNormalised = objectProperties.size();

        ArrayList<OWLDataProperty> dataProperties = new ArrayList<OWLDataProperty>();
        this.naive.dataPropertiesInSignature(Imports.EXCLUDED).forEach(dataProperties::add);
        this.nOfDataPropertiesInNormalised = dataProperties.size();
    }
}
