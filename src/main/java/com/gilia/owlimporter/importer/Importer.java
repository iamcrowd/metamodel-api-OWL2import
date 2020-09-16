package com.gilia.owlimporter.importer;

import com.gilia.metamodel.*;

import org.json.simple.JSONObject;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.builder.metabuilder.*;

import com.gilia.owlimporter.importer.ClassExpressionTools;
import com.gilia.owlimporter.importer.axiom.classAxiom.SubClassOf;


import com.gilia.exceptions.EmptyOntologyException;


import www.ontologyutils.toolbox.AnnotateOrigin;
import www.ontologyutils.toolbox.FreshAtoms;
import www.ontologyutils.toolbox.Utils;
import www.ontologyutils.normalization.NormalizationTools;
import www.ontologyutils.normalization.Normalization;


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
	
	
	/**
	 * 
	 * @param iri, a String containing an Ontology URI 
	 * @param precompute, true if you want reasoning over ontology before importing. Otherwise, false.
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
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
				this.onto = reasoner.getRootOntology();
			}
			
			this.metabuilder = new MetaConverter();
		}
		catch (Exception e){
        	e.printStackTrace();
    	}
	}
	
	/**
	 * 
	 * @param iri, a String containing an Ontology URI 
	 * @param precompute, true if you want reasoning over ontology before importing. Otherwise, false.
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
				reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					  					  InferenceType.CLASS_ASSERTIONS,
            							  InferenceType.DISJOINT_CLASSES, 
            							  InferenceType.OBJECT_PROPERTY_HIERARCHY, 
            							  InferenceType.OBJECT_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_ASSERTIONS,
            							  InferenceType.DATA_PROPERTY_HIERARCHY);
				this.onto = reasoner.getRootOntology();
			}
			
			this.metabuilder = new MetaConverter();
		}
		catch (Exception e){
        	e.printStackTrace();
		}
	}
	
	
	/**
	 * Starting to loop over ontology axioms to normalise
	 * Filter axiom types http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/model/AxiomType.html
	 * 
	 * @implNote we remove unsupported class expressions not removed by ontology utils dependency
	 * 
	 * @param ontology
	 * @return a normalised ontology to be imported
	 */
	public OWLOntology normalizeToImport(OWLOntology ontology) {
		FreshAtoms.resetFreshAtomsEquivalenceAxioms(); // optional; for verification purpose

		OWLOntology copy = Utils.newEmptyOntology();
		copy.addAxioms(ontology.axioms());
		
		OWLOntology unsupported = Utils.newEmptyOntology();

		Stream<OWLAxiom> tBoxAxioms = copy.tboxAxioms(Imports.EXCLUDED);
		tBoxAxioms.forEach((ax) -> {
			copy.remove(ax);

			try {
				OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
				OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
				
				if ((left.getClassExpressionType() == ClassExpressionType.OBJECT_HAS_VALUE) || 
							(right.getClassExpressionType() == ClassExpressionType.OBJECT_HAS_VALUE)) {
						
						unsupported.addAxiom(ax);
				} else {
					copy.addAxioms(NormalizationTools.asSubClassOfAxioms(ax));
				}
			}
			catch (Exception f) {
					System.out.println("Unsupported axioms:" + ax.toString());
			}
		
		});
		
		System.out.println("\nNaive Normalized TBox");
		//OWLOntology naive = null;
		OWLOntology naive = Utils.newEmptyOntology();
		naive = Normalization.normalizeNaive(copy);
		
		System.out.println("\n ************List Unsupported ClassExpressions and Axioms in Normalization App\n");
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
		
		naive.tboxAxioms(Imports.EXCLUDED).forEach(ax -> System.out.println(Utils.pretty("-- " + ax.toString())));
		
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
	
	public JSONObject showOntology() {
		JSONObject jsonAx = new JSONObject();
    	Iterator<OWLAxiom> axs = this.onto.axioms().iterator();
    	while (axs.hasNext()) {
    		OWLAxiom ax = axs.next();
    		jsonAx.put(ax.getAxiomType().toString(), ax.toString());
        }
    	return jsonAx;
	}
	
	/**
	 * Metamodel instance is to exported as a JSONObject according to the KF JSON Scheme
	 * 
	 * @return JSONObject KF metamodel
	 */
	public JSONObject toJSON() {
		return this.metabuilder.generateJSON(this.kfimported);
	}
	
	
	/**
	 * Import OWL Classes and generate a KF instance with the respective set of ObjectTypes
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void class2KF() {
   	  	ClassExpressionTools import_classes = new ClassExpressionTools();
   	  	import_classes.owlClasses(this.kfimported, this.onto);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
	/**
	 * Import All SubClasses and generate a KF instance with the respective set of ObjectTypes and
	 * Subsumptions
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void OWLSubClassesImport() {
   	  	SubClassOf import_subclasses = new SubClassOf();
	  	import_subclasses.owlSubClassAxiom2Subsumptions(this.kfimported,this.onto);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
	/**
	 * Import All SubClasses for a given Class and generate a KF instance with the respective set of ObjectTypes and
	 * Subsumptions
	 * 
	 * @see KF metamodel ObjectType
	 */
	public void OWLSubClassesImport(IRI anIRI) {
   	  	SubClassOf import_subclasses = new SubClassOf();
	  	import_subclasses.owlSubClassAxiomForGivenOWLClass2Subsumptions(this.kfimported, this.onto, anIRI);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
	
	public void importNormalisedOntology() {
		NormalFormTools tools = new NormalFormTools();
		tools.asKF(this.kfimported, this.onto);
	  	MetaBuilder builder = new MetaConverter();
	  	builder.generateJSON(this.kfimported);
	}
	
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
	
}