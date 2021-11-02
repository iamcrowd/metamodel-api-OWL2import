package com.gilia.controllers;

import com.gilia.exceptions.*;
import com.gilia.owlimporter.importer.*;
import com.gilia.utils.*;
import org.everit.json.schema.*;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import static com.gilia.utils.Constants.*;

/**
 * Controller of the OWL_NORMAL_TO_META_ROUTE endpoint. This controller is in charge of
 * receiving an OWL spec, creating the Metamodel instance and returning a
 * Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class OWLNormalisedToMetaController {

    @PostMapping(
            value = OWL_NORMAL_TO_META_ROUTE
    // consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    // produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity owlNormalisedToMeta(
            @RequestParam(value = "onto", required = false) String iriAsString,
            @RequestParam(value = "reasoning", required = false) Boolean precompute,
            @RequestParam(value = "ontoFile", required = false) MultipartFile[] ontoFile
    ) {
        JSONObject result;

        try {
            if (precompute == null) {
                precompute = false;
            }
            Importer importer;
            if (ontoFile != null && ontoFile.length >= 1) {
                if (ontoFile.length == 1 && ontoFile[0] != null && !ontoFile[0].isEmpty()) {
                    importer = new Importer(ontoFile[0], precompute);
                    importer.importNormalisedOntology();
                    result = importer.toJSONMetrics();
                } else {
                    result = new JSONObject();
                    JSONObject success = new JSONObject();
                    JSONArray failed = new JSONArray();
                    for (MultipartFile onto : ontoFile) {
                        try {
                            System.out.println("Start importing ontology: " + onto.getOriginalFilename());
                            importer = new Importer(onto, precompute);
                            importer.importNormalisedOntology();
                            success.put(onto.getOriginalFilename(), importer.toJSONMetrics());
                            System.out.println("Finished import ontology: " + onto.getOriginalFilename());
                        } catch (Exception e) {
                            failed.add(onto.getOriginalFilename());
                            System.out.println("Can't import ontology: " + onto.getOriginalFilename());
                        }
                    }
                    result.put("success", success);
                    result.put("failed", failed);
                }
            } else if (iriAsString != null && iriAsString != "") {
                importer = new Importer(IRI.create(iriAsString), precompute);
                importer.importNormalisedOntology();
                result = importer.toJSONMetrics();
            } else {
                return new ResponseEntity<>("No ontology sensed", HttpStatus.BAD_REQUEST);
            }
        } catch (JSONException e) {
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            e.getCausingExceptions()
                    .stream()
                    .map(ValidationException::getMessage)
                    .forEach(stringBuilder::append);
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    stringBuilder.toString()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (MetamodelException e) {
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (EmptyOntologyException e) {
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
