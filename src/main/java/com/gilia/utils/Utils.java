package com.gilia.utils;

import org.json.simple.JSONArray;

public class Utils {

    /**
     * Concatenates multiple JSONArrays
     *
     * @param arrs
     * @return
     */
    private JSONArray concatArray(JSONArray... arrs) { // TODO: Test
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.size(); i++) {
                result.add(arr.get(i));
            }
        }
        return result;
    }
}
