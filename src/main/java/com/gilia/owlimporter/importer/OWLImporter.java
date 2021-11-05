package com.gilia.owlimporter.importer;

import com.gilia.builder.metabuilder.*;
import com.gilia.metamodel.*;
import com.gilia.owlimporter.importer.axtoKF.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.json.simple.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.*;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;

import org.springframework.web.multipart.*;
import uk.ac.manchester.cs.jfact.*;
import www.ontologyutils.normalization.*;
import www.ontologyutils.toolbox.*;

import static com.gilia.utils.ImportUtils.validateOWL;

/**
 * Allow translate OWL to KF metamodel by applying axioms translations.
 * Preserving the semantics of the original ontology.
 */
public class OWLImporter {

    private Metamodel metamodel;
    private MetaConverter converter;
    private OWLOntology ontology;
    private OWLOntology supported;
    private OWLOntology unsupported;
    private OWLOntologyManager manager;
    private boolean reasoning;
    private static final OWLReasonerFactory reasonerFactoryFact = new JFactFactory();
    private List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();

    private JSONObject metrics;

    public OWLImporter(boolean reasoning) {
        this.reset();
        this.converter = new MetaConverter();
        this.manager = OWLManager.createOWLOntologyManager();
        this.reasoning = reasoning;
    }

    /**
     * Reset resultant metamodel, supported and unsupported axioms for load a
     * new ontology.
     */
    public void reset() {
        this.metamodel = new Metamodel();
        this.supported = Utils.newEmptyOntology();
        this.unsupported = Utils.newEmptyOntology();
        this.metrics = new JSONObject();

        //reset metrics parameters
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
     * @param path a String containing a file path to an Ontology File.
     */
    public void load(String path) {
        try {
            this.reset();
            File file = new File(path);
            validateOWL(file);
            this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
        } catch (Exception e) {
            System.out.println("Error loading ontology with path: " + path + ". (" + e.getMessage() + ")");
        }
    }

    /**
     *
     * @param multipartFile a MultiparFile from a FormData containing an
     * Ontology File.
     */
    public void load(MultipartFile multipartFile) {
        try {
            this.reset();
            File file = new File("src/main/resources/temporalOWL.tmp");
            try ( OutputStream os = new FileOutputStream(file)) {
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
           // OWLReasonerFactory factory = new JFactFactory();
            OWLReasoner reasoner = reasonerFactoryFact.createReasoner(this.ontology);
            /*reasoner.precomputeInferences(
                    InferenceType.CLASS_HIERARCHY,
                    InferenceType.CLASS_ASSERTIONS,
                    InferenceType.DISJOINT_CLASSES,
                    InferenceType.OBJECT_PROPERTY_HIERARCHY,
                    InferenceType.OBJECT_PROPERTY_ASSERTIONS,
                    InferenceType.DATA_PROPERTY_ASSERTIONS,
                    InferenceType.DATA_PROPERTY_HIERARCHY
            );*/

     //       List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
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
     * Check if an Axiom is entailed in the current ontology
     * @param ontology
     * @param axiom
     * @return
     */
	private boolean isEntailed(OWLAxiom axiom) {
		this.gens.contains(axiom);
		if (this.gens.contains(axiom)) {
			System.out.println("Entailed? Si");
			return true;
		}
		else {
			System.out.println("Entailed? NO");
			return false;
		}
	}

    /**
     * Translation to KF metamodel. It does reasoning first and then translate
     * each supported axiom and register both: supported and unsupported axioms.
     */
    public void translate() {
        long start, end;
        start = Calendar.getInstance().getTimeInMillis();

        if (this.reasoning) {
            //reason over the input ontology
            this.precompute();
        }

        //get all tbox axioms
        Set<OWLAxiom> tboxAxioms = this.ontology.tboxAxioms(Imports.EXCLUDED).collect(Collectors.toSet());

        //iterate each axiom
        tboxAxioms.forEach((axiom) -> {
            try {
                //determine if axiom is of type SubClassOf
                if (axiom.isOfType(AxiomType.SUBCLASS_OF)) {
                    //get left and right expressions (SubClass -> SuperClass)
                    OWLClassExpression left = ((OWLSubClassOfAxiom) axiom).getSubClass();
                    OWLClassExpression right = ((OWLSubClassOfAxiom) axiom).getSuperClass();

                    //check if axiom is of type 1 (A -> B)
                    if (NormalForm.typeOneSubClassAxiom(left, right)) {
                        if (NormalForm.isAtom(left) && NormalForm.isAtom(right)) {
                            // atom -> atom
                            Ax1A ax1A = new Ax1A();
                            ax1A.type1AasKF(this.metamodel, left, right);
                        } else if (NormalForm.isAtom(left) && NormalForm.isDisjunctionOfAtoms(right)) {
                            // atom -> disjunction
                            Ax1B ax1B = new Ax1B();
                            ax1B.type1BasKF(this.metamodel, left, right);
                        } else {
                            throw new EmptyStackException();
                        }
                        
                     } else if (NormalForm.typeThreeSubClassAxiom(left, right)) {
                            OWLObjectPropertyExpression property = ((OWLObjectAllValuesFrom) right).getProperty();
                            OWLObjectProperty namedProperty = property.getNamedProperty();
                            
                            if (property.isNamed()) {
                            	IRI.create("http://www.w3.org/2002/07/owl#Thing");
                            	Collection<OWLClassExpression> sub = new ArrayList<>();
                            	OWLAxiom exists = new OWLSubClassOfAxiomImpl(
                            			((OWLClassExpression) new OWLClassImpl(IRI.create("http://www.w3.org/2002/07/owl#Thing"))),
                            			((OWLClassExpression) new OWLObjectSomeValuesFromImpl(property, 
                            											new OWLClassImpl(IRI.create("http://www.w3.org/2002/07/owl#Thing")))),
                            			new ArrayList<OWLAnnotation>());
                        		
                            	System.out.println("Check entailment");
                            	if (this.isEntailed(exists)) {
                            		System.out.println("It is entailed exists property");
                            		Ax3 ax3asKF = new Ax3();
                            		ax3asKF.type3asKF(this.metamodel, left, right);
                            	} else {
                            		System.out.println("Not entailed exists property");
                            	}
                            }
                        	
                    } else {
                        throw new EmptyStackException();
                    }
                } else {
                    throw new EmptyStackException();
                }

                this.supported.addAxioms(axiom);
                this.metrics.put("supportedAxiomsCount", ((int) this.metrics.get("supportedAxiomsCount")) + 1);
            } catch (Exception e) {
                this.unsupported.addAxiom(axiom);
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
        int[] index = {1};

        this.unsupported.tboxAxioms(Imports.EXCLUDED).forEachOrdered((axiom) -> {
            axioms.put("axiom" + index[0], axiom.toString());
            index[0]++;
        });
        return axioms;
    }

    /**
     * Export KF metamodel result from importation.
     *
     * @return JSONObject with the result of translation: metamodel, metrics and
     * unsupported axioms.
     */
    public JSONObject toJSON() {
        JSONObject values = new JSONObject();

        values.put("kf", this.converter.generateJSON(metamodel));
        values.put("metrics", this.metrics);
        values.put("unsupported", this.getUnsupportedAxioms());

        return values;
    }
}