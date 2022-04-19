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
                System.out.println("Starting import ontology: " + getOntologyName(ontologies[0], input, 1));
                loadOntology(ontologies[0], input, importer);
                importer.translate();
                result = importer.toJSON();
                System.out.println("Finished import ontology: " + getOntologyName(ontologies[0], input, 1));
            } else if (ontologies.length > 1) {
                result = new JSONObject();
                JSONObject success = new JSONObject();
                JSONArray failed = new JSONArray();
                int index = 0;
                for (Object ontology : ontologies) {
                    try {
                        System.out.println("Starting import ontology: " + getOntologyName(ontology, input, index));
                        loadOntology(ontologies[0], input, importer);
                        importer.translate();
                        success.put(getOntologyName(ontology, input, index), importer.toJSON());
                        System.out.println("Finished import ontology: " + getOntologyName(ontology, input, index));
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
        } catch (JSONException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.toString());
            JSONObject jsonError = error.toJSONObject();
            jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(stringBuilder::append);
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), stringBuilder.toString());
            JSONObject jsonError = error.toJSONObject();
            jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        } catch (MetamodelException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.toString());
            JSONObject jsonError = error.toJSONObject();
            jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        } catch (EmptyOntologyException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.toString());
            JSONObject jsonError = error.toJSONObject();
            jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.toString());
            JSONObject jsonError = error.toJSONObject();
            jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void loadOntology(Object ontology, String input, OWLImporter importer) {
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
    }

    private String getOntologyName(Object ontology, String input, int index) {
        return input.equals("uri")
                ? (String) ontology
                : input.equals("string")
                        ? "ontology" + index
                        : ((MultipartFile) ontology).getOriginalFilename();
    }

}
