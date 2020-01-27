package com.gilia.controllers;

import com.gilia.builder.MetaDirector;
import com.gilia.exceptions.NoMetamodelCreatedException;
import com.gilia.metamodel.Metamodel;
import com.gilia.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.FileNotFoundException;
import static com.gilia.utils.Constants.UML_SCHEMA_PATH;

@RestController
public class UmlToMetaController {

    @GetMapping("/umltometa")
    public JSONObject umlToMeta(@RequestBody String payload) {

        MetaDirector director = new MetaDirector();
        JSONParser parser = new JSONParser();
        JSONObject response = new JSONObject();

        try {
            //Utils.validateJSON(payload, UML_SCHEMA_PATH);
            JSONObject umlModelObject = (JSONObject) parser.parse(payload);
            director.createMetamodelFromUML(umlModelObject);
            response = director.generateMeta();
        } catch (ParseException | NoMetamodelCreatedException e){
            response.put("error", e.getMessage());
        }

        return response;

    }
}
