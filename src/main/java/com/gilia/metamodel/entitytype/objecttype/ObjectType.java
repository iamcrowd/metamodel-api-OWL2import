package com.gilia.metamodel.entitytype.objecttype;

import com.gilia.metamodel.entitytype.EntityType;
import org.json.simple.JSONArray;

/**
 *
 * @author Emiliano Rios Gavagnin
 */
public class ObjectType extends EntityType { // TODO: 1:1 Mapping

    /**
     *
     * @param objectTypeName
     */
    public ObjectType(String objectTypeName) {
        this.name = objectTypeName;
    }

    /**
     *
     * @return
     */
    public JSONArray toJSONArray(){
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(name);
        return jsonArray;
    }
}
