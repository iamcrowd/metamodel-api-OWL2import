package com.gilia.controllers;

import static com.gilia.utils.Constants.*;

import com.gilia.exceptions.*;
import com.gilia.owlimport.*;
import com.gilia.utils.*;

import org.apache.commons.lang3.exception.*;
import org.everit.json.schema.*;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

/**
 * Controller of the OWL_TO_META_ROUTE endpoint. This controller is in charge of
 * receiving an OWL spec, creating the Metamodel instance and returning a
 * Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class OWLToMetaController {

    @PostMapping(value = OWL_TO_META_ROUTE)
    public ResponseEntity owlToMeta(
            @RequestParam(value = "ontologies", required = false) Object[] ontologies,
            @RequestParam(value = "reasoner", required = false, defaultValue = "") String reasoner,
            @RequestParam(value = "input", required = true, defaultValue = "string") String input,
            @RequestParam(value = "filtering", required = false, defaultValue = "true") Boolean filtering) {
        JSONObject result;

        try {
            OWLImporter importer = new OWLImporter();
            importer.setFiltering(filtering);

            if (!reasoner.equals("")) {
                System.out.println("Loading reasoner: " + reasoner);
                importer.loadReasoner(reasoner);
            }
            if (ontologies.length == 1) {
                System.out.println("Starting ontology importation: " + getOntologyName(ontologies[0], input, 1));
                loadOntology(ontologies[0], input, importer, 1);
                System.out.println("Starting ontology translation: " + getOntologyName(ontologies[0], input, 1));
                importer.translate();
                System.out.println("Finished ontology translation: " + getOntologyName(ontologies[0], input, 1));
                System.out.println("Starting ontology serialization: " + getOntologyName(ontologies[0], input, 1));
                result = importer.toJSON();
                System.out.println("Finished ontology serialization: " + getOntologyName(ontologies[0], input, 1));
                System.out.println("Finished ontology importation: " + getOntologyName(ontologies[0], input, 1));
            } else if (ontologies.length > 1) {
                result = new JSONObject();
                JSONObject success = new JSONObject();
                JSONArray failed = new JSONArray();
                int index = 0;
                for (Object ontology : ontologies) {
                    try {
                        System.out.println("Starting ontology importation: " + getOntologyName(ontology, input, index));
                        loadOntology(ontologies[0], input, importer, index);
                        System.out.println("Starting ontology translation: " + getOntologyName(ontology, input, index));
                        importer.translate();
                        System.out.println("Finished ontology translation: " + getOntologyName(ontology, input, index));
                        System.out.println(
                                "Starting ontology serialization: " + getOntologyName(ontology, input, index));
                        success.put(getOntologyName(ontology, input, index), importer.toJSON());
                        System.out.println(
                                "Finished ontology serialization: " + getOntologyName(ontology, input, index));
                        System.out.println("Finished ontology importation: " + getOntologyName(ontology, input, index));
                    } catch (Exception e) {
                        failed.add(getOntologyName(ontology, input, index));
                        System.out.println("Can't import ontology: " + getOntologyName(ontology, input, index));
                    }
                    index++;
                }
                result.put("success", success);
                result.put("failed", failed);
            } else {
                return new ResponseEntity<>("There is needed at least one ontology.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            JSONObject jsonError = error.toJSONObject();
            if (e != null && e.getCause() != null)
                jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e.getCause()));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void loadOntology(Object ontology, String input, OWLImporter importer, int index) throws Exception {
        System.out.println("Starting ontology loading: " + getOntologyName(ontology, input, index));
        switch (input) {
            case "string":
                importer.load((String) ontology);
                break;
            case "uri":
                importer.load(IRI.create((String) ontology));
                break;
            case "file":
                importer.load((MultipartFile) ontology);
                break;
        }
        System.out.println("Finished ontology loading: " + getOntologyName(ontology, input, index));
    }

    private String getOntologyName(Object ontology, String input, int index) throws Exception {
        return input.equals("uri")
                ? (String) ontology
                : input.equals("string")
                        ? "ontology" + index
                        : ((MultipartFile) ontology).getOriginalFilename();
    }

}
