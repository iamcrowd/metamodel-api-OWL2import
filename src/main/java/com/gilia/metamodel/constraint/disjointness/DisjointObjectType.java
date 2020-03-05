package com.gilia.metamodel.constraint.disjointness;

import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.KEY_ENTITIES;
import static com.gilia.utils.Constants.KEY_NAME;

/**
 * Representation of the Disjoint object types class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class DisjointObjectType extends DisjointnessConstraint {

    protected ArrayList<ObjectType> entities;

    /**
     * @param name
     */
    public DisjointObjectType(String name) {
        super(name);
        this.entities = null;
    }

    /**
     * @param entities
     */
    public DisjointObjectType(ArrayList<ObjectType> entities) {
        super();
        this.entities = entities;
    }

    /**
     * @param name
     * @param entities
     */
    public DisjointObjectType(String name, ArrayList<ObjectType> entities) {
        super(name);
        this.entities = entities;
    }

    public ArrayList<ObjectType> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<ObjectType> entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DisjointObjectType that = (DisjointObjectType) o;
        return Objects.equals(entities, that.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entities);
    }

    @Override
    public String toString() {
        return "DisjointObjectType{" +
                "entities=" + entities +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * @return
     */
    public JSONObject toJSONObject() {
        JSONObject constraint = new JSONObject();
        JSONArray entitiesName = new JSONArray();

        for (ObjectType entity : entities) {
            String name = (String) entity.getName();
            entitiesName.add(name);
        }

        constraint.put(KEY_NAME, name);
        constraint.put(KEY_ENTITIES, entitiesName);

        return constraint;
    }
}
