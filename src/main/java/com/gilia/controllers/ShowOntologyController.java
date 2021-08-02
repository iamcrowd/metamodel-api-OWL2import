package com.gilia.controllers;

import com.gilia.exceptions.*;
import com.gilia.owlimporter.importer.*;
import com.gilia.utils.*;
import org.everit.json.schema.*;
import org.json.*;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import static com.gilia.utils.Constants.*;

/**
 * Controller of the OWL_TO_META_ROUTE endpoint. This controller is in charge of
 * receiving an OWL spec, creating the Metamodel instance and returning a
 * Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class ShowOntologyController {

    @PostMapping(
            value = OWL_SHOW_ONTOLOGY_ROUTE
    // consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    // produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity showOntology(
            @RequestParam("onto") String iriAsString,
            @RequestParam("reasoning") Boolean precompute,
            @RequestParam(value = "ontoFile", required = false) MultipartFile ontoFile
    ) {
        JSONObject result;

        try {
            Importer importer;
            if (ontoFile != null && !ontoFile.isEmpty()) {
                importer = new Importer(ontoFile, precompute);
            } else {
                importer = new Importer(IRI.create(iriAsString), precompute);
            }
            result = importer.showOntology();
        } catch (JSONException e) {
            ResponseError error = new ResponseError(
                    HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    e.getMessage()
            );
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            e
                    .getCausingExceptions()
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
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
