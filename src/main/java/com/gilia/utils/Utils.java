package com.gilia.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Utils {

    /**
     * Concatenates multiple JSONArrays
     *
     * @param arrs
     * @return
     */
    public JSONArray concatArray(JSONArray... arrs) { // TODO: Test
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.size(); i++) {
                result.add(arr.get(i));
            }
        }
        return result;
    }

    public static void validateJSON(String jsonString, String schemaPath) throws FileNotFoundException {
        File schemaFile = new File(schemaPath);
        InputStream targetStream = new FileInputStream(schemaFile);

        JSONObject jsonSchema = new JSONObject(new JSONTokener(targetStream));

        JSONObject jsonSubject = new JSONObject(jsonString);
        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonSubject);
    }


}
