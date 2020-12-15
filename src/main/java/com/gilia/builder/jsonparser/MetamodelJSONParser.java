package com.gilia.builder.jsonparser;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.*;

public class MetamodelJSONParser {

    private JSONObject metamodelJSONToParse;

    public MetamodelJSONParser(JSONObject metamodelJSONToParse) {
        this.metamodelJSONToParse = metamodelJSONToParse;
    }

    public JSONObject getMetamodelJSONToParse() {
        return metamodelJSONToParse;
    }

    public void setMetamodelJSONToParse(JSONObject metamodelJSONToParse) {
        this.metamodelJSONToParse = metamodelJSONToParse;
    }

    public JSONObject getMetamodelJSONEntityTypes() {
        return (JSONObject) metamodelJSONToParse.get(StringUtils.capitalize(KEY_ENTITY_TYPE));
    }

    public JSONArray getMetamodelJSONObjectTypes() {
        return (JSONArray) this.getMetamodelJSONEntityTypes().get(StringUtils.capitalize(KEY_OBJECT_TYPE));
    }

    public JSONArray getMetamodelJSONDataTypes() {
        return (JSONArray) this.getMetamodelJSONEntityTypes().get(StringUtils.capitalize(KEY_DATA_TYPE));
    }

    public JSONObject getMetamodelJSONValueProperties() {
        return (JSONObject) this.getMetamodelJSONEntityTypes()
                .get(StringUtils.capitalize(KEY_VALUE_PROPERTY));
    }

    public JSONArray getMetamodelJSONValueTypes() {
        return (JSONArray) this.getMetamodelJSONValueProperties()
                .get(StringUtils.capitalize(KEY_VALUE_TYPE));
    }


}
