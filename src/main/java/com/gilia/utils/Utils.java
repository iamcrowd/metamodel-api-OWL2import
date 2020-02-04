package com.gilia.utils;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Random;

import static com.gilia.utils.Constants.CHARSET;
import static com.gilia.utils.Constants.RANDOM_STRING_REGEX;

/**
 * Class use for utility methods that can be used anywhere in the application. The methods inside this class
 * are public and static, and will facilitate coding complex methods/functions
 */
public class Utils {

    /**
     * Validates the given jsonString against a JSON Schema already defined in a file. The path to this file
     * must be provided as a parameter. The method will execute successfully (no exceptions will be thrown)
     * if the jsonString is valid according to the JSON Schema. If the jsonString is not valid, then it will
     * throw a ValidationException.
     *
     * @param jsonString JSON String that contains an stringify JSON Object
     * @param schemaPath The path to a file that contains a JSON Schema definition
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateJSON(String jsonString, String schemaPath) throws FileNotFoundException, ValidationException, JSONException {
        File schemaFile = new File(schemaPath);
        InputStream targetStream = new FileInputStream(schemaFile);

        JSONObject jsonSchema = new JSONObject(new JSONTokener(targetStream));

        JSONObject jsonSubject = new JSONObject(jsonString);
        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonSubject);
    }

    /**
     * Generates a random string with alphanumeric characters (UTF-8). The length of the string is specified by parameter.
     *
     * @param stringLength Length of the string to be generated
     * @return Random string of the given length with alphanumeric characters
     */
    public static String getAlphaNumericString(int stringLength) {

        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, Charset.forName(CHARSET));

        // Create a StringBuffer to store the result
        StringBuffer stringBuffer = new StringBuffer();

        // remove all spacial char
        String AlphaNumericString
                = randomString
                .replaceAll(RANDOM_STRING_REGEX, "");

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < AlphaNumericString.length(); k++) {
            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (stringLength > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (stringLength > 0)) {

                stringBuffer.append(AlphaNumericString.charAt(k));
                stringLength--;
            }
        }

        // return the resultant string
        return stringBuffer.toString();
    }

}
