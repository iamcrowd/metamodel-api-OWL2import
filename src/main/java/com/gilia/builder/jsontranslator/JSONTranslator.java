package com.gilia.builder.jsontranslator;

import com.gilia.metamodel.Metamodel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Interface that allows a class to generate a Metamodel instance from a JSON.
 * The JSON must represent a model in a modeling language such as UML, EER, ORM or even the Metamodel itself.
 * In any case, the JSON must respect the schema defined for that language in particular. This interface represents
 * the builder in the Builder design pattern.
 *
 * @author Emiliano Rios Gavagnin
 */
public interface JSONTranslator {

    /**
     * Creates the classes of the metamodel that corresponds with the elements of the model being represented
     * by the given json.
     *
     * @param json JSONObject that represents a model defined in a modeling language and respects the language schema
     *
     * @return A Metamodel instance that represents a translation of the given model into metamodel classes.
     */
    Metamodel createMetamodel(JSONObject json);
}
