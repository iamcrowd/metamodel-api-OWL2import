package com.gilia.controllers;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.ImportUtils.validateOWL;
import static com.gilia.utils.Utils.validateJSON;

import com.gilia.builder.MetaDirector;
import com.gilia.exceptions.EmptyOntologyException;
import com.gilia.exceptions.MetamodelException;
import com.gilia.owlimporter.importer.Importer;
import com.gilia.owlimporter.importer.classExpression.ClassExpression;
import com.gilia.utils.ResponseError;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller of the OWL_TO_META_ROUTE endpoint. This controller is in charge of receiving an OWL spec, creating the Metamodel
 * instance and returning a Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class ShowNormalisedOntologyController {

  @PostMapping(
    value = OWL_SHOW_NORMALISED_ONTOLOGY_ROUTE
    // consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    // produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity showNormalisedOntology(
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
      importer.importNormalisedOntology();
      result = importer.showOntology(importer.getNaive());
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
