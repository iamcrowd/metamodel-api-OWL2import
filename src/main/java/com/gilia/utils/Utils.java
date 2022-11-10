package com.gilia.utils;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import org.everit.json.schema.*;
import org.everit.json.schema.loader.*;
import org.json.*;
import org.json.simple.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;

import static com.gilia.utils.Constants.CHARSET;
import static com.gilia.utils.Constants.RANDOM_STRING_REGEX;

/**
 * Class use for utility methods that can be used anywhere in the application.
 * The methods inside this class are public and static, and will facilitate
 * coding complex methods/functions
 */
public class Utils {

    /**
     * Validates the given jsonString against a JSON Schema already defined in a
     * file. The path to this file must be provided as a parameter. The method
     * will execute successfully (no exceptions will be thrown) if the
     * jsonString is valid according to the JSON Schema. If the jsonString is
     * not valid, then it will throw a ValidationException.
     *
     * @param jsonString JSON String that contains an stringify JSON Object
     * @param schemaPath The path to a file that contains a JSON Schema
     *                   definition
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateJSON(String jsonString, String schemaPath)
            throws FileNotFoundException, ValidationException, JSONException {
        File schemaFile = new File(schemaPath);
        InputStream targetStream = new FileInputStream(schemaFile);

        org.json.JSONObject jsonSchema = new org.json.JSONObject(new org.json.JSONTokener(targetStream));

        org.json.JSONObject jsonSubject = new org.json.JSONObject(jsonString);
        Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(jsonSubject);
    }

    /**
     * Validates the given OWL 2 spec defined in an OWL file. The path to this
     * file must be provided as a parameter. The method will execute
     * successfully (no exceptions will be thrown) if the OWL 2 file is valid
     * according to the W3C standard. If the OWL 2 spec is not valid, then it
     * will throw a ValidationException. File path is given as this example:
     * "C:\\pizza.owl.xml"
     *
     * @param owl2FilePath The path to a file that contains an OWL spec (OWL/XML
     *                     | RDF/XML)
     * @throws FileNotFoundException
     * @throws ValidationException
     */
    public static void validateOWL(String owl2FilePath) throws FileNotFoundException, ValidationException {
        try {
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            File file = new File(owl2FilePath);
            OWLOntology o = man.loadOntologyFromOntologyDocument(file);
            System.out.println(o);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Generates a random string with alphanumeric characters (UTF-8). The
     * length of the string is specified by parameter.
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
        String AlphaNumericString = randomString
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

    /**
     * Short print of exception message with reduced stack trace.
     * 
     * @param message
     * @param e
     */
    public static void printException(String message, Exception e) {
        System.out.println(message +
                " => " + e.toString() + " at " + e.getStackTrace()[0].getFileName() +
                " (" + e.getStackTrace()[0].getLineNumber() + ")");
    }

    public static String prettyJSON(org.json.simple.JSONObject json) {
        return new org.json.JSONObject(json).toString(2).replaceAll("\\\\", "");
    }
}
