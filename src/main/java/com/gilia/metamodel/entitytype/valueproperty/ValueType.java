package com.gilia.metamodel.entitytype.valueproperty;

import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.*;

/**
 * @author Emiliano Rios Gavagnin
 */
public class ValueType extends ValueProperty {

    /**
     * @param name
     */
    public ValueType(String name) {
        super(name);
    }

    /**
     * Generates a JSONObject that represents the information of the Object type according to the
     * ORM language. The JSONObject generated respects the ORM Schema.
     *
     * @return JSONObject that represents the equivalent ORM Entity.
     */
    public JSONObject toORM() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, KEY_VALUE);
        return jsonObject;
    }
}
