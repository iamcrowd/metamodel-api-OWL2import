package com.gilia.metamodel.entitytype.objecttype;

import com.gilia.metamodel.entitytype.EntityType;
import org.json.simple.JSONObject;

/**
 * @author Emiliano Rios Gavagnin
 */
public class ObjectType extends EntityType {

    public ObjectType() {
        super();
    }

    /**
     * @param name
     */
    public ObjectType(String name) {
        super(name);
    }

    /**
     * @return
     */
    public String toString() {
        return "ObjectType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * @return
     */
    public JSONObject toJSONObject() {
        // TODO: Implement
        return new JSONObject();
    }
}
