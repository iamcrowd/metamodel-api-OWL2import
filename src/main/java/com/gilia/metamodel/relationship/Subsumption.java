package com.gilia.metamodel.relationship;

import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the Subsumption class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Subsumption extends Relationship {

    protected ObjectType parent;

    /**
     * Creates a basic instance of a Subsumption. It will be created only with the name of the subsumption. Any other information will be missing.
     *
     * @param name String that represents the name of the subsumption
     */
    public Subsumption(String name) {
        super(name);
        this.parent = null;
    }

    /**
     * Creates an instance of a Subsumption. This constructor receives information about the name of the subsumption,
     * the parent entity, and the entities involved in the subsumption (children)
     *
     * @param name String that represents the name of the subsumption
     * @param parent ObjectType that represents the parent entity
     * @param entities ArrayList of ObjectType that represents the children entities
     */
    public Subsumption(String name, ObjectType parent, ArrayList<ObjectType> entities) {
        super(name, entities, null);
        this.parent = parent;
    }

    public ObjectType getParent() {
        return parent;
    }

    public void setParent(ObjectType parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subsumption that = (Subsumption) o;
        return Objects.equals(parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parent);
    }

    @Override
    public String toString() {
        return "Subsumption{" +
                "parent=" + parent +
                ", entities=" + entities +
                ", roles=" + roles +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Creates a JSONObject with the relevant information about the Subsumption object. The JSON format is based on the
     * Metamodel JSON Schema.
     * <p>
     * Example: <p>
     * { <p>
     * "entity children": [ <p>
     * "http://crowd.fi.uncoma.edu.ar#Class4", <p>
     * "http://crowd.fi.uncoma.edu.ar#Class3" <p>
     * ], <p>
     * "name": "http://crowd.fi.uncoma.edu.ar#s1", <p>
     * "entity parent": "http://crowd.fi.uncoma.edu.ar#Class2" <p>
     * }
     *
     * @return JSONObject with information about the Subsumption object
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject subsumption = new JSONObject();
        JSONArray entitiesJSON = new JSONArray();

        for (Object entity : entities) {
            entitiesJSON.add(((ObjectType) entity).getName());
        }

        subsumption.put(KEY_NAME, name);
        subsumption.put(KEY_ENTITY_PARENT, parent.getName());
        subsumption.put(KEY_ENTITY_CHILDREN, entitiesJSON);
        return subsumption;
    }
}
