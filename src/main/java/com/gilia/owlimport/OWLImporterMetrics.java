package com.gilia.owlimport;

import java.util.Calendar;
import java.util.stream.Stream;

import org.json.simple.*;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

@SuppressWarnings("unchecked")
public class OWLImporterMetrics {

    private JSONObject metrics;
    private JSONObject translationMetrics;
    private JSONObject ontologyMetrics;
    private JSONObject reasonedOntologyMetrics;

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
        this.ontologyMetrics = new JSONObject();
        this.reasonedOntologyMetrics = new JSONObject();

        this.metrics.put("translation", this.translationMetrics);
        this.translationMetrics.put("translationTime", null);
        this.translationMetrics.put("supportedAxiomsCount", 0);
        this.translationMetrics.put("unsupportedAxiomsCount", 0);
        this.translationMetrics.put("filteredAxiomsCount", 0);
        this.translationMetrics.put("axiom1ACount", 0);
        this.translationMetrics.put("axiom1BCount", 0);
        this.translationMetrics.put("axiomComplementOfCount", 0);
        this.translationMetrics.put("axiom2Count", 0);
        this.translationMetrics.put("axiom3Count", 0);

        this.metrics.put("ontology", this.ontologyMetrics);
        this.ontologyMetrics.put("logicAxiomsCount", 0);
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
            this.reasonedOntologyMetrics.put("entitiesCount", 0);
            this.reasonedOntologyMetrics.put("classesCount", 0);
            this.reasonedOntologyMetrics.put("objectPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("dataPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("annotationPropertiesCount", 0);
            this.reasonedOntologyMetrics.put("datatypesCount", 0);
            this.reasonedOntologyMetrics.put("namedIndividualsCount", 0);
        }
    }

    public void add(String metric, String set) {
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) + 1);
    }

    public void add(String metric, String set, int value) {
        ((JSONObject) (this.metrics.get(set))).put(metric,
                (int) ((JSONObject) (this.metrics.get(set))).get(metric) + value);
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
        Stream<OWLAxiom> tBoxAxioms = ontology.tboxAxioms(Imports.EXCLUDED);
        tBoxAxioms.forEach((axiom) -> {
            if (axiom.isLogicalAxiom()) {
                this.add("logicAxiomsCount", reasoned ? "reasonedOntology" : "ontology");
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
}
