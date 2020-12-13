package com.gilia.builder.jsonparser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.KEY_ENTITIES;
import static com.gilia.utils.Constants.KEY_RELATIONSHIPS;

public class ORMJSONParser {

    private JSONObject ORMJSONToParse;

    public ORMJSONParser(JSONObject metamodelJSONToParse) {
        this.ORMJSONToParse = metamodelJSONToParse;
    }

    public JSONObject getORMJSONToParse() {
        return ORMJSONToParse;
    }

    public void setORMJSONToParse(JSONObject ORMJSONToParse) {
        this.ORMJSONToParse = ORMJSONToParse;
    }

    public JSONArray getORMJSONEntities() {
        return (JSONArray) ORMJSONToParse.get(KEY_ENTITIES);
    }

    public JSONArray getORMJSONRelationships() {
        return (JSONArray) ORMJSONToParse.get(KEY_RELATIONSHIPS);
    }
}
