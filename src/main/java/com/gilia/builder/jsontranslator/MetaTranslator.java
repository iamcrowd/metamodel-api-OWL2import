package com.gilia.builder.jsontranslator;

import com.gilia.metamodel.Metamodel;
import org.json.simple.JSONObject;

/**
 * Represents a concrete builder in the Builder design pattern.
 * This class is in charge of building the Metamodel instance according to a Metamodel JSON.
 */
public class MetaTranslator implements JSONTranslator {
    @Override
    public Metamodel createMetamodel(JSONObject json) {
        return null;
    }
}
