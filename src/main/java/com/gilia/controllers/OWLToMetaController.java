package com.gilia.controllers;

import static com.gilia.utils.Constants.*;

import com.gilia.exceptions.*;
import com.gilia.owlimport.*;
import com.gilia.utils.*;
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
    public ResponseEntity owlToMeta(@RequestParam(value = "ontologyUri", required = false) String ontologyUri,
            @RequestParam(value = "ontologyString", required = false) String ontologyString,
            @RequestParam(value = "ontologiesFiles", required = false) MultipartFile[] ontologiesFiles,
            @RequestParam(value = "reasoner", required = false, defaultValue = "") String reasoner,
            @RequestParam(value = "input", required = true, defaultValue = "string") String input,
            @RequestParam(value = "filtering", required = false, defaultValue = "true") Boolean filtering) {
        JSONObject result;

        try {
            OWLImporter importer = new OWLImporter();
            importer.setFiltering(filtering);
            if (!reasoner.equals("")) {
                importer.loadReasoner(reasoner);
            }
            if (input.equals("files") && ontologiesFiles != null && ontologiesFiles.length >= 1) {
                if (ontologiesFiles.length == 1 && ontologiesFiles[0] != null && !ontologiesFiles[0].isEmpty()) {
                    importer.load(ontologiesFiles[0]);
                    importer.translate();
                    result = importer.toJSON();
                } else {
                    result = new JSONObject();
                    JSONObject success = new JSONObject();
                    JSONArray failed = new JSONArray();
                    for (MultipartFile ontologyFile : ontologiesFiles) {
                        try {
                            System.out.println("Starting import ontology: " + ontologyFile.getOriginalFilename());
                            importer.load(ontologyFile);
                            importer.translate();
                            success.put(ontologyFile.getOriginalFilename(), importer.toJSON());
                            System.out.println("Finished import ontology: " + ontologyFile.getOriginalFilename());
                        } catch (Exception e) {
                            failed.add(ontologyFile.getOriginalFilename());
                            System.out.println("Can't import ontology: " + ontologyFile.getOriginalFilename());
                        }
                    }
                    result.put("success", success);
                    result.put("failed", failed);
                }
            } else if (input.equals("string") && ontologyString != null && ontologyString != "") {
                importer.load(ontologyString);
                importer.translate();
                result = importer.toJSON();
            } else if (input.equals("uri") && ontologyUri != null && ontologyUri != "") {
                importer.load(IRI.create(ontologyUri));
                importer.translate();
                result = importer.toJSON();
            } else {
                return new ResponseEntity<>("There is needed an ontology URI, String or File/s.",
                        HttpStatus.BAD_REQUEST);
            }
        } catch (JSONException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(stringBuilder::append);
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), stringBuilder.toString());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (MetamodelException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (EmptyOntologyException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
