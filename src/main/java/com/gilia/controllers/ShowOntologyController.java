package com.gilia.controllers;

import com.gilia.builder.MetaDirector;
import com.gilia.exceptions.MetamodelException;
import com.gilia.exceptions.EmptyOntologyException;
import com.gilia.utils.ResponseError;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.HermiT.*;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.validateJSON;
import static com.gilia.utils.ImportUtils.validateOWL;

import com.gilia.owlimporter.importer.Importer;
import com.gilia.owlimporter.importer.classExpression.Class;

/**
 * Controller of the OWL_TO_META_ROUTE endpoint. This controller is in charge of receiving an OWL spec, creating the Metamodel
 * instance and returning a Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class ShowOntologyController {

    @PostMapping(value = OWL_SHOW_ONTOLOGY_ROUTE, 
	consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity showOntology(@RequestParam("onto") String iriAsString, 
			   						   @RequestParam("reasoning") Boolean precompute) {
        JSONObject result;

        try {
        	Importer importer = new Importer(IRI.create(iriAsString), precompute);
        	result = importer.showOntology();
        	
        } catch (JSONException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (ValidationException e) {
            StringBuilder stringBuilder = new StringBuilder();
            e.getCausingExceptions().stream()
                    .map(ValidationException::getMessage)
                    .forEach(stringBuilder::append);
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), stringBuilder.toString());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (MetamodelException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        } catch (EmptyOntologyException e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
