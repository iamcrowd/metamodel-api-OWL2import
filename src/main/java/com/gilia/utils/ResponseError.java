package com.gilia.utils;

import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;

import static com.gilia.utils.Constants.*;

/**
 * Contains information about a server-side error. This class is mainly used
 * to inform the client of an error.
 */
public class ResponseError {

    int status;
    String error;
    String message;

    /**
     * Creates an instance of an error. The timestamp is generated when calling the toJSONObject method.
     *
     * @see com.gilia.utils.ResponseError#toJSONObject()
     * @param status HTTP status code
     * @param error HTTP reason
     * @param message Custom error message. A brief description.
     */
    public ResponseError(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Creates a JSONObject with the error information and a timestamp.
     *
     * @return JSONObject with error information
     */
    public JSONObject toJSONObject(){
        Date date= new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);

        JSONObject json = new JSONObject();

        json.put(TIMESTAMP_STRING, ts.toString());
        json.put(STATUS_STRING, status);
        json.put(ERROR_STRING, error);
        json.put(MESSAGE_STRING, message);

        return json;
    }
}
