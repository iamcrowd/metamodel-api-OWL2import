package com.gilia.metamodel.entitytype;

import com.gilia.metamodel.Entity;
import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.*;


import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

/**
 * @author Emiliano Rios Gavagnin
 */
public abstract class EntityType extends Entity {

    public EntityType() {
        super();
    }

    /**
     * @param name
     */
    public EntityType(String name) {
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
        jsonObject.put(KEY_TYPE, ENTITY_STRING);
        return jsonObject;
    }
    
}
