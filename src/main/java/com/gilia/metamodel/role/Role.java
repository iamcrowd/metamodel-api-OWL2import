package com.gilia.metamodel.role;

import com.gilia.metamodel.Entity;
import org.json.simple.JSONObject;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Role extends Entity { // TODO: 1:1 Mapping

    // Entity type
    // Cardinality

    /**
     *
     */
    public Role() {
        super();

    }

    /**
     * @param name
     */
    public Role(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Role{" +
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
