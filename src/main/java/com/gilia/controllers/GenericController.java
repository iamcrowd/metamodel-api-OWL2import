package com.gilia.controllers;

import com.gilia.builder.MetaDirector;
import com.gilia.exceptions.MetamodelException;
import com.gilia.exceptions.NotValidLanguageException;
import com.gilia.utils.ResponseError;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.validateJSON;

@RestController
@CrossOrigin(origins = "*")
public class GenericController {

    @PostMapping("/{operation}")
    public ResponseEntity convertModel(@PathVariable String operation, @RequestBody String payload) {
        MetaDirector director = new MetaDirector();
        JSONParser parser = new JSONParser();
        JSONObject response;

        if (operation.matches(OPERATIONS_REGEX)) {
            String[] languages = operation.split("to");
            String fromLanguage = languages[0];
            String toLanguage = languages[1];
            String schemaPath = SCHEMAS_PATH + fromLanguage + "Schema.json";
            
            try {
                validateJSON(payload, schemaPath);
                JSONObject jsonPayload = (JSONObject) parser.parse(payload);
                director.createMetamodel(jsonPayload, fromLanguage);
                response = director.generateModel(toLanguage);
            } catch (FileNotFoundException e) {
                ResponseError error = new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
                return new ResponseEntity<>(error.toJSONObject(), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (JSONException | ParseException e) {
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
            } catch (NotValidLanguageException e){
                ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
                return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), MODEL_CONVERSION_NOT_SUPPORTED);
            return new ResponseEntity<>(error.toJSONObject(), HttpStatus.BAD_REQUEST);
        }
    }
}
