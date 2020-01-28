package com.gilia.controllers;

import com.gilia.builder.MetaDirector;
import com.gilia.exceptions.MetamodelException;
import org.everit.json.schema.ValidationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.validateJSON;

@RestController
public class UmlToMetaController {

    @GetMapping(UML_TO_META_ROUTE)
    public JSONObject umlToMeta(@RequestBody String payload) {

        MetaDirector director = new MetaDirector();
        JSONParser parser = new JSONParser();
        JSONObject response = new JSONObject();

        try {
            validateJSON(payload, UML_SCHEMA_PATH);
            JSONObject umlModelObject = (JSONObject) parser.parse(payload);
            director.createMetamodelFromUML(umlModelObject);
            response = director.generateMeta();
        } catch (ParseException | MetamodelException | FileNotFoundException e) {
            response.put(ERROR_STRING, e.getMessage());
        }

        return response;
    }
}
