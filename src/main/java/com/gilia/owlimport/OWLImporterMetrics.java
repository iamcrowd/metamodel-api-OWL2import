package com.gilia.owlimport;

import static com.gilia.utils.Constants.URI_IMPORT_CONCEPT;
import static com.gilia.utils.Utils.*;

import java.util.Calendar;
import java.util.stream.Stream;

import com.gilia.builder.metabuilder.MetaConverter;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;

import org.json.simple.*;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

@SuppressWarnings("unchecked")
public class OWLImporterMetrics {

    private JSONObject metrics;
    private JSONObject translationMetrics;
    private JSONObject unsupportedMetrics;
    private JSONObject ontologyMetrics;
    private JSONObject reasonedOntologyMetrics;
    private JSONObject KFMetrics;

    private boolean reasoned = false;

    public OWLImporterMetrics() {
        this.reset();
    }

    /*
     * Reset metrics parameters
     */
    public void reset() {
        this.metrics = new JSONObject();
        this.translationMetrics = new JSONObject();
        this.unsupportedMetrics = new JSONObject();
        this.ontologyMetrics = new JSONObject();
        this.reasonedOntologyMetrics = new JSONObject();
        this.KFMetrics = new JSONObject();

        this.metrics.put("translation", this.translationMetrics);
        this.translationMetrics.put("translationTime", null);
        this.translationMetrics.put("supportedAxiomsCount", 0);
        this.translationMetrics.put("unsupportedAxiomsCount", 0);
        this.translationMetrics.put("filteredAxiomsCount", 0);
        this.translationMetrics.put("axiomSubClassOfCount", 0);
        this.translationMetrics.put("axiomUnionOfCount", 0);
        this.translationMetrics.put("axiomComplementOfCount", 0);
        this.translationMetrics.put("axiomExistsCount", 0);
        this.translationMetrics.put("axiomForAllCount", 0);

        this.metrics.put("unsupported", this.unsupportedMetrics);

        this.metrics.put("ontology", this.ontologyMetrics);
        this.ontologyMetrics.put("logicAxiomsCount", 0);
        this.ontologyMetrics.put("tBoxLogicAxiomsCount", 0);
        this.ontologyMetrics.put("entitiesCount", 0);
        this.ontologyMetrics.put("classesCount", 0);
        this.ontologyMetrics.put("objectPropertiesCount", 0);
        this.ontologyMetrics.put("dataPropertiesCount", 0);
        this.ontologyMetrics.put("annotationPropertiesCount", 0);
        this.ontologyMetrics.put("datatypesCount", 0);
        this.ontologyMetrics.put("namedIndividualsCount", 0);

        if (reasoned) {
            this.metrics.put("reasonedOntology", this.reasonedOntologyMetrics);
            this.reasonedOntologyMetrics.put("logicAxiomsCount", 0);
            this.reasonedOntologyMetrics.put("tBoxLogicAxiomsCount", 0);
            this.reasonedOntologyMetrics.put("entitiesCount", 0);
            this.reasonedOntologyMetrics.put("classesCount", 0);
            this.reasonedOntologyMetrics.put("objectPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("dataPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("annotationPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("datatypesCount", 0);
            this.reasonedOntologyMetrics.put("namedIndividualsCount", 0);
        }

        this.metrics.put("KF", this.KFMetrics);
        this.KFMetrics.put("objectTypesCount", 0);
        this.KFMetrics.put("relationshipsCount", 0);
        this.KFMetrics.put("subsumptionsCount", 0);
        this.KFMetrics.put("rolesCount", 0);
        this.KFMetrics.put("disjointnessCount", 0);
        this.KFMetrics.put("completenessCount", 0);
        this.KFMetrics.put("cardinalitiesCount", 0);
        this.KFMetrics.put("freshPrimitivesCount", 0);
    }

    public void add(String metric, String set) {
        if (((JSONObject) (this.metrics.get(set))).get(metric) == null) {
            ((JSONObject) (this.metrics.get(set))).put(metric, 0);
        }
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) + 1);
    }

    public void add(String metric, String set, int value) {
        if (((JSONObject) (this.metrics.get(set))).get(metric) == null) {
            ((JSONObject) (this.metrics.get(set))).put(metric, 0);
        }
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) + value);
    }

    public void addUnsupportedAxiom(OWLAxiom axiom) {
        this.add("unsupportedAxiomsCount", "translation");
        this.add(axiom.getAxiomType().toString(), "unsupported");
    }

    public void remove(String metric, String set) {
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) - 1);
    }

    public void remove(String metric, String set, int value) {
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) - value);
    }

    public void startTimer(String metric, String set) {
        ((JSONObject) (this.metrics.get(set))).put(metric, (double) Calendar.getInstance().getTimeInMillis());
    }

    public void stopTimer(String metric, String set) {
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (double) ((double) Calendar.getInstance().getTimeInMillis()
                        - (double) ((JSONObject) (this.metrics.get(set))).get(metric)) / 1000.0);
    }

    public void set(String metric, String set, Object value) {
        ((JSONObject) (this.metrics.get(set))).put(metric, value);
    }

    public void setReasoned(boolean reasoned) {
        this.reasoned = reasoned;
    }

    public void get(String metric, String set) {
        ((JSONObject) (this.metrics.get(set))).get(metric);
    }

    public JSONObject get() {
        return this.metrics;
    }

    public void countLogicAxioms(OWLOntology ontology, boolean reasoned) {
        this.set("logicAxiomsCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.getLogicalAxiomCount(Imports.EXCLUDED));
    }

    public void countTBoxLogicAxioms(OWLOntology ontology, boolean reasoned) {
        Stream<OWLAxiom> tBoxAxioms = ontology.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach((axiom) -> {
            if (axiom.isLogicalAxiom()) {
                this.add("tBoxLogicAxiomsCount", reasoned ? "reasonedOntology" : "ontology");
            }
        });
    }

    public void countEntities(OWLOntology ontology, boolean reasoned) {
        this.set("entitiesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.signature(Imports.EXCLUDED).count());
    }

    public void countClasses(OWLOntology ontology, boolean reasoned) {
        this.set("classesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.classesInSignature(Imports.EXCLUDED).count());
    }

    public void countObjectProperties(OWLOntology ontology, boolean reasoned) {
        this.set("objectPropertiesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.objectPropertiesInSignature(Imports.EXCLUDED).count());
    }

    public void countDataProperties(OWLOntology ontology, boolean reasoned) {
        this.set("dataPropertiesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.dataPropertiesInSignature(Imports.EXCLUDED).count());
    }

    public void countAnnotationProperties(OWLOntology ontology, boolean reasoned) {
        this.set("annotationPropertiesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.annotationPropertiesInSignature(Imports.EXCLUDED).count());
    }

    public void countDatatypes(OWLOntology ontology, boolean reasoned) {
        this.set("datatypesCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.datatypesInSignature(Imports.EXCLUDED).count());
    }

    public void countNamedIndividuals(OWLOntology ontology, boolean reasoned) {
        this.set("namedIndividualsCount", reasoned ? "reasonedOntology" : "ontology",
                ontology.individualsInSignature(Imports.EXCLUDED).count());
    }

    public void calculateOntologyMetrics(OWLOntology ontology, boolean reasoned) throws Exception {
        try {
            this.countLogicAxioms(ontology, reasoned);
            this.countTBoxLogicAxioms(ontology, reasoned);
            this.countEntities(ontology, reasoned);
            this.countClasses(ontology, reasoned);
            this.countObjectProperties(ontology, reasoned);
            this.countDataProperties(ontology, reasoned);
            this.countAnnotationProperties(ontology, reasoned);
            this.countDatatypes(ontology, reasoned);
            this.countNamedIndividuals(ontology, reasoned);
        } catch (Exception e) {
            printException("Exception during " + (reasoned ? "reasoned " : "")
                    + "ontology metrics calculation (calculateOntologyMetrics)", e);
            throw new Exception("Exception during " + (reasoned ? "reasoned " : "") + "ontology metrics calculation.",
                    e);
        }
    }

    public void calculateKFMetrics(MetaConverter converter, Metamodel metamodel) throws Exception {
        try {
            this.set("objectTypesCount", "KF", converter.getNofObjectTypes());
            this.set("relationshipsCount", "KF", converter.getNofRels());
            this.set("subsumptionsCount", "KF", converter.getNofSubsumptions());
            this.set("rolesCount", "KF", converter.getNofRoles());
            this.set("disjointnessCount", "KF", converter.getNofDisjointC());
            this.set("completenessCount", "KF", converter.getNofCompletenessC());
            this.set("cardinalitiesCount", "KF", converter.getNofCardinalities());

            for (Entity entity : metamodel.getEntities()) {
                if (entity.getName().startsWith(URI_IMPORT_CONCEPT)) {
                    this.add("freshPrimitivesCount", "KF");
                }
            }
        } catch (Exception e) {
            printException("Exception during KF metrics calculation (calculateKFMetrics)", e);
            throw new Exception("Exception during KF metrics calculation.", e);
        }
    }
}
