package com.gilia.builder.metabuilder;

import com.gilia.metamodel.Metamodel;
import org.json.simple.JSONObject;

/**
 * Interface that allows a class to generate a JSONObject that represents a model in a modeling language from the Metamodel.
 * The JSON must represent a model in a modeling language such as UML, EER, ORM or even the Metamodel itself.
 * In any case, the JSON must respect the schema defined for that language in particular. This interface represents
 * the builder in the Builder design pattern.
 *
 * @author Emiliano Rios Gavagnin
 */
public interface MetaBuilder {

    /**
     * Generates the JSONObject according to the instance of the metamodel given.
     * The translation is determined by the destination modeling language desired.
     * This is determined by the implementation of this method.
     *
     * @param metamodel A Metamodel instance
     *
     * @return A JSONObject that represents the model generated from the metamodel instance
     * given in a modeling language determined by this method implementation
     */
    JSONObject generateJSON(Metamodel metamodel);
}
