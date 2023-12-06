package com.gilia.ontologyrepair;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.semanticweb.owl.explanation.api.*;
import org.semanticweb.owl.explanation.impl.blackbox.BlackBoxExplanationGenerator2;
import org.semanticweb.owl.explanation.impl.blackbox.SimpleContractionStrategy;
import org.semanticweb.owl.explanation.impl.blackbox.StructuralExpansionStrategy;
import org.semanticweb.owl.explanation.impl.blackbox.checker.SatisfiabilityEntailmentCheckerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.owllink.OWLlinkHTTPXMLReasonerFactory;
import org.semanticweb.owlapi.owllink.OWLlinkReasonerConfigurationImpl;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.*;

import com.gilia.builder.MetaDirector;
import com.gilia.builder.metabuilder.MetaConverter;
import com.gilia.metamodel.*;
import com.gilia.metamodel.constraint.*;
import com.gilia.metamodel.constraint.disjointness.*;
import com.gilia.metamodel.relationship.*;
import com.gilia.metamodel.role.*;
import com.gilia.utils.Constants;

import openllet.owlapi.OpenlletReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;

public class MetaRepairer {

    private Metamodel metamodel;
    private MetaDirector director = new MetaDirector();
    private JSONParser parser = new JSONParser();

    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private OWLDataFactory datafactory;
    private String reasonerName;

    private List<OWLAxiom> tbox;
    private List<OWLAxiom> filteredtbox;
    private HashMap<OWLAxiom, Entity> tboxMap;
    private Set<Explanation<OWLAxiom>> explanations;

    private int maxExplanations;
    private boolean precompute;
    private boolean filtering;

    // private List<List<OWLAxiom>> mups;
    // private int maxMups;
    // private int minSetSize;

    public MetaRepairer(int maxExplanations, boolean precompute, boolean filtering) {
        this.director = new MetaDirector();
        this.manager = OWLManager.createOWLOntologyManager();
        this.datafactory = OWLManager.getOWLDataFactory();

        this.maxExplanations = maxExplanations;
        this.precompute = precompute;
        this.filtering = filtering;

        // this.mups = new ArrayList<>();
        // this.maxMups = maxMups;
        // this.minSetSize = minSetSize;
    }

    /**
     * 
     * @param string a String containing an Metamodel JSON.
     */
    public void loadMetamodel(String meta) throws Exception {
        try {
            director.createMetamodelFromMeta((JSONObject) parser.parse(meta));
            this.metamodel = director.getMetamodel();
        } catch (Exception e) {
            printException("Exception during metamodel loading (loadMetamodel), with Object: " + meta, e);
            throw new Exception("Exception during metamodel loading.", e);
        }
    }

    /**
     *
     * @param string a String containing an Ontology RDF/XML file.
     */
    public void loadOntology(String string) throws Exception {
        try {
            this.ontology = this.manager
                    .loadOntologyFromOntologyDocument(new ByteArrayInputStream(string.getBytes("UTF-8")));
            // this.ontology.getDeclarationAxioms().forEach(importDeclaration -> {
            // this.manager.applyChange(new RemoveImport(this.ontology, importDeclaration));
            // });
            if (this.ontology.isEmpty()) {
                throw new IllegalArgumentException("Ontology is empty.");
            }
        } catch (Exception e) {
            printException("Exception during ontology loading (loadOntology), with String: " + string, e);
            throw new Exception("Exception during ontology loading.", e);
        }
    }

    /**
     * Loads the reasoner to be used by precompute method later.
     * 
     * @param reasonerName a String containing the name of the reasoner to be
     *                     loaded.
     */
    public void loadReasoner(String reasonerName) throws Exception {
        try {
            this.reasonerName = reasonerName.equals("") ? "pellet" : reasonerName;
        } catch (Exception e) {
            printException("Exception during reasoner loading (loadReasoner)", e);
            throw new Exception("Exception during reasoner loading (" + reasonerName + ").", e);
        }
    }

    /**
     * Creates a reasoner for the input ontology with the reasoner configured in.
     */
    private OWLReasoner getReasoner(OWLOntology ontology) throws Exception {
        try {
            OWLReasoner reasoner;
            switch (this.reasonerName) {
                case Constants.JFACT:
                    OWLReasonerFactory reasonerFactoryFact = new JFactFactory();
                    reasoner = reasonerFactoryFact.createReasoner(ontology);
                    break;
                case Constants.RACER:
                    // Racer
                    // Run the tool as OWLlink server in the commandline. ./Racer -protocol OWLlink
                    OWLlinkHTTPXMLReasonerFactory reasonerFactoryRacer = new OWLlinkHTTPXMLReasonerFactory();
                    OWLlinkReasonerConfigurationImpl reasonerRacerConfiguration = new OWLlinkReasonerConfigurationImpl(
                            this.setReasonerServer());
                    reasoner = reasonerFactoryRacer.createReasoner(ontology, reasonerRacerConfiguration);
                    break;
                case Constants.KONCLUDE:
                    // Konclude
                    // Run the tool as OWLlink server in the commandline. ./Konclude owllinkserver
                    // -p 8080
                    OWLlinkHTTPXMLReasonerFactory reasonerFactoryKonclude = new OWLlinkHTTPXMLReasonerFactory();
                    OWLlinkReasonerConfigurationImpl reasonerKoncludeConfiguration = new OWLlinkReasonerConfigurationImpl(
                            this.setReasonerServer());
                    reasoner = reasonerFactoryKonclude.createReasoner(ontology,
                            reasonerKoncludeConfiguration);
                    break;
                case Constants.PELLET:
                default:
                    OWLReasonerFactory reasonerFactoryPellet = new OpenlletReasonerFactory();
                    reasoner = reasonerFactoryPellet.createReasoner(ontology);
                    break;
            }
            return reasoner;
        } catch (Exception e) {
            printException("Exception during reasoner generation for sub ontology (getReasoner)", e);
            throw new Exception("Exception during reasoner generation for sub ontology (" + reasonerName + ").", e);
        }
    }

    /**
     * Returns a reasoner factory for the reasoner name configured in.
     * 
     */
    private OWLReasonerFactory getReasonerFactory() throws Exception {
        try {
            OWLReasonerFactory reasonerFactory;
            switch (this.reasonerName) {
                case Constants.JFACT:
                    reasonerFactory = new JFactFactory();
                    break;
                case Constants.RACER:
                    reasonerFactory = new OWLlinkHTTPXMLReasonerFactory();
                    break;
                case Constants.KONCLUDE:
                    reasonerFactory = new OWLlinkHTTPXMLReasonerFactory();
                    break;
                case Constants.PELLET:
                default:
                    reasonerFactory = new OpenlletReasonerFactory();
                    break;
            }
            return reasonerFactory;
        } catch (Exception e) {
            printException("Exception during reasoner factory generation (getReasonerFactory)", e);
            throw new Exception("Exception during reasoner factory generation (" + reasonerName + ").", e);
        }
    }

    /**
     * Sets the reasoner server URL for ther reasoners that require it: Racer and
     * Konclude.
     * 
     * @return URL of the reasoner server.
     */
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
    private void precompute(OWLReasoner reasoner) throws Exception {
        try {
            List<InferredAxiomGenerator<? extends OWLAxiom>> axiomsGens = new ArrayList<>();

            axiomsGens.add(new InferredSubClassAxiomGenerator());
            axiomsGens.add(new InferredClassAssertionAxiomGenerator());
            axiomsGens.add(new InferredDisjointClassesAxiomGenerator());
            axiomsGens.add(new InferredEquivalentClassAxiomGenerator());
            axiomsGens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
            axiomsGens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
            axiomsGens.add(new InferredInverseObjectPropertiesAxiomGenerator());
            axiomsGens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
            axiomsGens.add(new InferredPropertyAssertionGenerator());
            axiomsGens.add(new InferredSubDataPropertyAxiomGenerator());
            axiomsGens.add(new InferredSubObjectPropertyAxiomGenerator());

            InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, axiomsGens);
            iog.fillOntology(this.datafactory, this.ontology);
        } catch (Exception e) {
            printException("Exception during precompute ontology (precompute) (" + reasonerName + ")", e);
            throw new Exception("Exception during precompute ontology (" + reasonerName + ").", e);
        }
    }

    /**
     * Generates the MUPS from ontology TBox and the entity and pair them with the
     * KF concepts.
     * 
     * @param entity a String containing the name of the entity to be repaired.
     */
    public boolean repair(String entity) throws Exception {
        try {
            // print metamodel
            System.out.println();
            System.out.println(new org.json.JSONObject(new MetaConverter().generateJSON(this.metamodel)).toString(4));
            System.out.println();

            // get the owl class of the entity to be repaired
            OWLClass entityClass = this.datafactory.getOWLClass(entity);
            OWLReasoner selectedReasoner = this.getReasoner(this.ontology);
            boolean isSatisfiable = selectedReasoner.isSatisfiable(entityClass);
            if (isSatisfiable) {
                System.out.println("The entity \"" + entityClass.getIRI().getFragment() + "\" IS satisfiable.");
                return false;
            } else {
                System.out.println("The entity \"" + entityClass.getIRI().getFragment() + "\" IS NOT satisfiable.");

                // precompute the ontology
                if (this.precompute) {
                    this.precompute(selectedReasoner);
                }

                // get all tbox axioms
                this.tbox = new ArrayList<OWLAxiom>(
                        this.ontology.getTBoxAxioms(Imports.EXCLUDED));

                // print all tbox axioms
                System.out.println("TBox Axioms: " + this.tbox.size());
                this.tbox.forEach(axiom -> System.out.println(axiom));
                System.out.println();

                // get filtered tbox axioms
                if (this.filtering) {
                    this.generateFilteredTBox();

                    // print all filteredtbox axioms
                    System.out.println("Filtered TBox Axioms: " + this.filteredtbox.size());
                    this.filteredtbox.forEach(axiom -> System.out.println(axiom));
                    System.out.println();
                }

                // map tbox axioms with KF entities
                this.mapNormalizedAxioms();

                // print map of tbox axioms with KF entities
                System.out.println("TBox Map: " + this.tboxMap.size());
                this.tboxMap.forEach((axiom, entityMap) -> System.out.println(axiom + " => " + entityMap));
                System.out.println();

                System.out.println("Start explanation generation...");

                // make a manager supplier for the explanation generator
                // the supplier is an object that is used as a callback function to return some
                // other object
                Supplier<OWLOntologyManager> managerSupplier = OWLManager::createOWLOntologyManager;

                // get tbox or filtered tbox axioms
                Set<OWLAxiom> axioms = filtering
                        ? this.filteredtbox.stream().collect(Collectors.toSet())
                        : this.tbox.stream().collect(Collectors.toSet());

                // create a explanation generator, specifically a black box explanation
                // generator
                BlackBoxExplanationGenerator2<OWLAxiom> generator = new BlackBoxExplanationGenerator2<OWLAxiom>(
                        axioms,
                        new SatisfiabilityEntailmentCheckerFactory(this.getReasonerFactory(), managerSupplier),
                        new StructuralExpansionStrategy<>(managerSupplier),
                        new SimpleContractionStrategy<>(),
                        new NullExplanationProgressMonitor<OWLAxiom>(),
                        managerSupplier);

                // create the entailment to be explained
                OWLAxiom entityEntailment = this.datafactory.getOWLSubClassOfAxiom(entityClass,
                        this.datafactory.getOWLNothing());

                // get our explanations
                explanations = generator.getExplanations(entityEntailment, this.maxExplanations);

                /*
                 * Supplier<OWLOntologyManager> supManager = () -> this.manager;
                 * 
                 * create the explanation generator factory which uses reasoners provided by the
                 * specified reasoner factory
                 * System.out.println("Reasoner factory: " + this.getReasonerFactory());
                 * 
                 * ExplanationGeneratorFactory<OWLAxiom> explanationGenFactory =
                 * ExplanationManager
                 * .createExplanationGeneratorFactory(this.getReasonerFactory(), () -> {
                 * return this.manager;
                 * });
                 * 
                 * // now create the actual explanation generator for our ontology
                 * ExplanationGenerator<OWLAxiom> explanationGen = explanationGenFactory
                 * .createExplanationGenerator(this.ontology.getTBoxAxioms(Imports.EXCLUDED));
                 * 
                 * OWLAxiom entityEntailment =
                 * this.datafactory.getOWLSubClassOfAxiom(entityClass,
                 * this.datafactory.getOWLNothing());
                 * 
                 * OWLAxiom entailment = this.tbox.get(0);
                 * System.out.println("Entailment: " + entailment);
                 * 
                 * get our explanations
                 * Set<Explanation<OWLAxiom>> explanation =
                 * explanationGen.getExplanations(entailment);
                 */

                System.out.println("Finished explanation generation.");

                // print explanations set
                System.out.println();
                System.out.println("Explanations: " + explanations.size());
                int explanationIndex = 1;
                for (Explanation<OWLAxiom> explanation : explanations) {
                    System.out.println("Explanation " + (explanationIndex < 10 ? "0" : "") + explanationIndex + ":");
                    System.out.println(explanation.getEntailment());
                    System.out.println(explanation.getAxioms());
                    System.out.println();
                    explanationIndex++;
                }

                /*
                 * // make all possible subsets of tbox axioms where the entity is
                 * unsatisfiable,
                 * // but if we remove at least one axiom of any subset the entity becomes
                 * // satisfiable and add them to the mups list
                 * System.out.println("Starting MUPS generation...");
                 * 
                 * generateMUPS(entityClass);
                 * 
                 * System.out.println("Finished MUPS generation.");
                 */

                return true;
            }
        } catch (Exception e) {
            printException("Exception during repair ontology (repair)", e);
            throw new Exception("Exception during repair ontology.", e);
        }
    }

    /**
     * Returns a list of TBox axioms without some filtered ones
     *
     * filter some tbox axioms:
     * - data property range (because attributes can't be unsatisfiable)
     * - data property domain (because attributes can't be unsatisfiable)
     * - object min/max cardinality (because cardinality restrictions can't be
     * unsatisfiable)
     * - data min/max cardinality (because cardinality restrictions can't be
     * unsatisfiable)
     * - subclass of owl:Nothing (because it not allow to generate explanations)
     * - equivalent classes (because it not allow to generate explanations)
     */
    private void generateFilteredTBox() throws Exception {
        this.filteredtbox = this.tbox.stream()
                .filter(axiom -> !axiom.isOfType(AxiomType.DATA_PROPERTY_RANGE)
                        && !axiom.isOfType(AxiomType.DATA_PROPERTY_DOMAIN)
                        && !(axiom.isOfType(AxiomType.SUBCLASS_OF)
                                && (((OWLSubClassOfAxiom) axiom).getSuperClass()
                                        .getClassExpressionType() == (ClassExpressionType.OBJECT_MIN_CARDINALITY)
                                        || ((OWLSubClassOfAxiom) axiom).getSuperClass()
                                                .getClassExpressionType() == (ClassExpressionType.OBJECT_MAX_CARDINALITY)
                                        || ((OWLSubClassOfAxiom) axiom).getSuperClass()
                                                .getClassExpressionType() == (ClassExpressionType.DATA_MIN_CARDINALITY)
                                        || ((OWLSubClassOfAxiom) axiom).getSuperClass()
                                                .getClassExpressionType() == (ClassExpressionType.DATA_MAX_CARDINALITY)))
                        && !(axiom.isOfType(AxiomType.SUBCLASS_OF) && ((OWLSubClassOfAxiom) axiom).getSuperClass()
                                .equals(this.datafactory.getOWLNothing()))
                        && !(axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)))
                .collect(Collectors.toList());
    }

    /**
     * Generates the MUPS from ontology Filtered TBox and the entity
     * 
     * @param entity a OWLClass of the entity to be repaired
     */
    /*
     * private void generateMUPS(OWLClass entity) throws Exception {
     * try {
     * // print filtered tbox axioms
     * System.out.println();
     * System.out.println("Filtered TBox Axioms: " + this.filteredtbox.size());
     * this.filteredtbox.forEach(axiom -> System.out.println(axiom));
     * System.out.println();
     * 
     * // make a set of sets of axioms that will be iterated
     * ArrayList<ArrayList<OWLAxiom>> iteratingSets = new ArrayList<>();
     * 
     * // first put sets with one of each axiom of tbox
     * // for (OWLAxiom axiom : this.filteredtbox) {
     * // ArrayList<OWLAxiom> mup = new ArrayList<OWLAxiom>();
     * // mup.add(axiom);
     * // iteratingSets.add(mup);
     * // }
     * 
     * // first we put all axioms of the tbox in the iteratingSets list
     * iteratingSets.add(new ArrayList<>(this.filteredtbox));
     * 
     * // in each iteration we have to check if each set is satisfiable with respect
     * to
     * // the entity and if it is not, we have to add it to the mups list
     * // if it is satisfiable, we have to add all possible combinations of the set
     * // with the rest of axioms of the tbox
     * while (!iteratingSets.isEmpty() && this.mups.size() < this.maxMups) {
     * ArrayList<ArrayList<OWLAxiom>> newIteratingSets = new ArrayList<>();
     * 
     * for (ArrayList<OWLAxiom> iteratingSet : iteratingSets) {
     * 
     * // if the mups set gets to the max size, we stop the generation
     * if (this.mups.size() >= this.maxMups)
     * break;
     * 
     * OWLOntology mupOntology = this.manager.createOntology(iteratingSet);
     * OWLReasoner mupReasoner = getReasoner(mupOntology);
     * boolean isMupSatisfiable = mupReasoner.isSatisfiable(entity);
     * 
     * if (!isMupSatisfiable && iteratingSet.size() > 1 && iteratingSet.size() >=
     * this.minSetSize) {
     * this.mups.add(iteratingSet);
     * } else {
     * for (int j = 0; j < this.filteredtbox.size(); j++) {
     * if (!iteratingSet.contains(this.filteredtbox.get(j))) {
     * ArrayList<OWLAxiom> newMup = new ArrayList<>();
     * newMup.addAll(iteratingSet);
     * newMup.add(this.filteredtbox.get(j));
     * 
     * boolean isContained = false;
     * 
     * for (ArrayList<OWLAxiom> array : newIteratingSets) {
     * if (array.containsAll(newMup)) {
     * isContained = true;
     * break;
     * }
     * }
     * 
     * if (!isContained)
     * newIteratingSets.add(newMup);
     * }
     * }
     * }
     * }
     * 
     * iteratingSets = newIteratingSets;
     * }
     * } catch (Exception e) {
     * printException("Exception during MUPS generation (generateMUPS)", e);
     * throw new Exception("Exception during MUPS generation.", e);
     * }
     * }
     */

    /*
     * Associates each Filtered TBox axioms with KF entities
     * (relationships (subsumptions), roles or constraints)
     * It's possible because the TBox axioms are generated from the KF metamodel
     * In other words, there are normalized axioms
     * 
     */
    private void mapNormalizedAxioms() throws Exception {
        try {
            String notNormalizedError = "Found not normalized axiom: ";

            this.tboxMap = new HashMap<>();

            // get the list of subsumption relationships on KF metamodel
            List<Subsumption> subsumptionRelationships = this.metamodel.getRelationships().stream()
                    .filter(relationship -> Subsumption.class.isInstance(relationship))
                    .map(relationship -> (Subsumption) relationship)
                    .collect(Collectors.toList());

            // get the list of attribute properties relationships on KF metamodel
            // List<AttributiveProperty> attributePropertiesRelationships =
            // this.metamodel.getRelationships().stream()
            // .filter(relationship -> AttributiveProperty.class.isInstance(relationship))
            // .map(relationship -> (AttributiveProperty) relationship)
            // .collect(Collectors.toList());

            // get the list of basic relationships on KF metamodel
            // List<Relationship> relationships = this.metamodel.getRelationships().stream()
            // .filter(relationship -> !Subsumption.class.isInstance(relationship)
            // && !AttributiveProperty.class.isInstance(relationship))
            // .collect(Collectors.toList());

            for (OWLAxiom axiom : (this.filtering ? this.filteredtbox : this.tbox)) {
                // check for subclass type axioms
                if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    // get the subclass axiom
                    OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
                    // get the subclass
                    OWLClassExpression subClass = subClassAxiom.getSubClass();
                    // get the superclass
                    OWLClassExpression superClass = subClassAxiom.getSuperClass();

                    // check if the subclass is a named class
                    if (!subClass.isNamed())
                        throw new Exception(notNormalizedError + axiom.toString());

                    // get the subclass entity
                    Entity subClassEntity = this.metamodel.getEntity(subClass.asOWLClass().getIRI().toString());
                    // check if the subclass entity is not null
                    if (subClassEntity == null)
                        throw new Exception(notNormalizedError + axiom.toString());

                    // if the superclass is a named class
                    if (superClass.isNamed()) {
                        if (superClass.isTopEntity()) {
                            // add the axiom to the map
                            this.tboxMap.put(axiom, subClassEntity);
                        } else {
                            // get the superclass entity
                            Entity superClassEntity = this.metamodel
                                    .getEntity(superClass.asOWLClass().getIRI().toString());
                            // check if the superclass entity is not null
                            if (superClassEntity == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // get the subsumption relationship
                            Subsumption subsumptionRelationship = subsumptionRelationships.stream()
                                    .filter(relationship -> relationship.getParent().equals(superClassEntity)
                                            && relationship.getChild().equals(subClassEntity))
                                    .findFirst().orElse(null);
                            // check if the subsumption relationship is not null
                            if (subsumptionRelationship == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // add the axiom to the map
                            this.tboxMap.put(axiom, subsumptionRelationship);
                        }
                    } else if (superClass.isAnonymous()) {
                        // check if the superclass is a ObjecSomeValuesFrom expression
                        if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                            // get the object property
                            OWLObjectSomeValuesFrom objectSomeValuesFrom = (OWLObjectSomeValuesFrom) superClass;

                            // get the object property entity
                            Entity objectPropertyEntity = this.metamodel.getEntity(
                                    objectSomeValuesFrom.getProperty().asOWLObjectProperty().getIRI().toString());
                            // check if the object property entity is not null
                            if (objectPropertyEntity == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // get the role
                            Role role = this.metamodel.getRoles().stream()
                                    .filter(r -> r.equals(objectPropertyEntity))
                                    .findFirst().orElse(null);
                            // check if the role is not null
                            if (role == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // add the axiom to the map
                            this.tboxMap.put(axiom, role);

                            // check if the superclass is a ObjectUnionOf expression
                        } else if (superClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                            // get the object union of
                            OWLObjectUnionOf objectUnionOf = (OWLObjectUnionOf) superClass;

                            // get the entity of the object union of
                            Entity objectUnionOfEntity = this.metamodel.getEntity(
                                    objectUnionOf.getOperandsAsList().get(0).asOWLClass().getIRI().toString());
                            // check if the object union of entity is not null
                            if (objectUnionOfEntity == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // get the constraint
                            Constraint constraint = this.metamodel.getConstraints().stream()
                                    .filter(c -> CompletenessConstraint.class.isInstance(c)
                                            && ((CompletenessConstraint) c).getEntities().contains(objectUnionOfEntity))
                                    .findFirst().orElse(null);
                            // check if the constraint is not null
                            if (constraint == null)
                                throw new Exception(notNormalizedError + axiom.toString());

                            // add the axiom to the map
                            this.tboxMap.put(axiom, constraint);
                        } else {
                            throw new Exception(notNormalizedError + axiom.toString());
                        }
                    }
                } else if (axiom.isOfType(AxiomType.DISJOINT_CLASSES)) {
                    // get the disjoint classes axiom
                    OWLDisjointClassesAxiom disjointClassesAxiom = (OWLDisjointClassesAxiom) axiom;

                    // get the disjoint classes
                    List<OWLClassExpression> disjointClasses = disjointClassesAxiom.getOperandsAsList();

                    // check if the disjoint classes are named classes
                    if (disjointClasses.stream().anyMatch(disjointClass -> !disjointClass.isNamed()))
                        throw new Exception(notNormalizedError + axiom.toString());

                    // get the disjoint classes entities
                    List<Entity> disjointClassesEntities = disjointClasses.stream()
                            .map(disjointClass -> this.metamodel
                                    .getEntity(disjointClass.asOWLClass().getIRI().toString()))
                            .collect(Collectors.toList());

                    // get the disjoint object type constraint
                    Constraint constraint = this.metamodel.getConstraints().stream()
                            .filter(c -> DisjointObjectType.class.isInstance(c)
                                    && ((DisjointObjectType) c).getEntities().containsAll(disjointClassesEntities))
                            .findFirst().orElse(null);

                    // check if the constraint is not null
                    if (constraint == null)
                        throw new Exception(notNormalizedError + axiom.toString());

                    // add the axiom to the map
                    this.tboxMap.put(axiom, constraint);
                } else {
                    throw new Exception(notNormalizedError + axiom.toString());
                }
            }
        } catch (Exception e) {
            printException("Exception during map normalized axioms (mapNormalizedAxioms)", e);
            throw new Exception("Exception during map normalized axioms.", e);
        }
    }

    /**
     * Short print of exception message with reduced stack trace.
     * 
     * @param message
     * @param e
     */
    public static void printException(String message, Exception e) {
        System.out.println(message +
                " => " + e.toString() + " at " + e.getStackTrace()[0].getFileName() +
                " (" + e.getStackTrace()[0].getLineNumber() + ")");
    }

    /**
     * Export results of repairing process to JSON.
     *
     * @return JSONObject with the results of repairing process.
     */
    public JSONObject toJSON() throws Exception {
        try {
            JSONObject result = new JSONObject();

            // add tbox to the result
            JSONObject tbox = new JSONObject();
            int tboxAxiomIndex = 1;
            for (OWLAxiom axiom : this.tbox) {
                tbox.put("axiom" + (tboxAxiomIndex < 10 ? "0" : "") + tboxAxiomIndex, axiom.toString());
                tboxAxiomIndex++;
            }
            result.put("TBox", tbox);

            // add filtered tbox to the result
            if (this.filtering) {
                JSONObject filteredTBox = new JSONObject();
                int filteredTBoxAxiomIndex = 1;
                for (OWLAxiom axiom : this.filteredtbox) {
                    filteredTBox.put("axiom" + (filteredTBoxAxiomIndex < 10 ? "0" : "") + filteredTBoxAxiomIndex,
                            axiom.toString());
                    filteredTBoxAxiomIndex++;
                }
                result.put("FilteredTBox", filteredTBox);
            }

            // add KF metamodel to the result
            result.put("KF", new MetaConverter().generateJSON(this.metamodel));

            // convert the mups list of lists of axioms to a JSONObject of JSONObject with
            // axiom strings
            // JSONObject mups = new JSONObject();
            // int mupIndex = 1;
            // for (List<OWLAxiom> mup : this.mups) {
            // JSONObject mupArray = new JSONObject();
            // int axiomIndex = 1;
            // for (OWLAxiom axiom : mup) {
            // mupArray.put("axiom" + (axiomIndex < 10 ? "0" : "") + axiomIndex,
            // axiom.toString());
            // axiomIndex++;
            // }
            // mups.put("MUP " + (mupIndex < 10 ? "0" : "") + mupIndex, mupArray);
            // mupIndex++;
            // }
            // result.put("MUPS", mups);

            // make a JSONObject with each explanation and its axioms
            JSONObject explanations = new JSONObject();
            int explanationIndex = 1;
            for (Explanation<OWLAxiom> explanation : this.explanations) {
                JSONObject explanationArray = new JSONObject();
                int axiomIndex = 1;
                for (OWLAxiom axiom : explanation.getAxioms()) {
                    explanationArray.put("axiom" + (axiomIndex < 10 ? "0" : "") + axiomIndex, axiom.toString());
                    axiomIndex++;
                }
                explanations.put("Explanation " + (explanationIndex < 10 ? "0" : "") + explanationIndex,
                        explanationArray);
                explanationIndex++;
            }
            result.put("Explanations", explanations);

            // make a JSONObject with each axiom and its KF entity mapping
            JSONObject tboxMap = new JSONObject();
            for (OWLAxiom axiom : this.tboxMap.keySet()) {
                tboxMap.put(axiom.toString(),
                        this.tboxMap.get(axiom).getClass().getSimpleName() + "@" + this.tboxMap.get(axiom).getName());
            }
            result.put("TBoxMap", tboxMap);

            return result;
        } catch (Exception e) {
            printException("Exception during ontology serialization (toJSON)", e);
            throw new Exception("Exception during ontology serialization.", e);
        }
    }
}
