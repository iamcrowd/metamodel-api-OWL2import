package com.gilia.metamodel.relationship;

import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Subsumption extends Relationship {  // TODO: 1:1 Mapping

    protected ObjectType parent;

    public Subsumption(String name) {
        super(name);
        this.parent = null;
    }

    public Subsumption(String name, ObjectType parent, ArrayList entities) {
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

    @Override
    public JSONObject toJSONObject() {
        JSONObject subsumption = new JSONObject();
        JSONArray entitiesJSON = new JSONArray();

        for (Object entity : entities){
            entitiesJSON.add(((ObjectType) entity).getName());
        }

        subsumption.put("name", name);
        subsumption.put("entity parent", parent.getName());
        subsumption.put("entity children", entitiesJSON);
        return subsumption;
    }
}
