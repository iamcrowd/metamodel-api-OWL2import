package com.gilia.metamodel.constraint.disjointness;

import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.KEY_ENTITIES;
import static com.gilia.utils.Constants.KEY_NAME;

/**
 * Representation of the Disjoint roles class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class DisjointRole extends DisjointnessConstraint {
    // TODO: Although this class is similar to DisjointObjectType,
    //  the implementation will be duplicated just in case they have different information.
    //  In the future, if no extra information is needed, the implementation can be done in
    //  the DisjointnessConstraint class

    protected ArrayList<ObjectType> entities;

    /**
     * @param name
     */
    public DisjointRole(String name) {
        super(name);
        this.entities = null;
    }

    /**
     * @param entities
     */
    public DisjointRole(ArrayList<ObjectType> entities) {
        super();
        this.entities = entities;
    }

    /**
     * @param name
     * @param entities
     */
    public DisjointRole(String name, ArrayList<ObjectType> entities) {
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
        DisjointRole that = (DisjointRole) o;
        return Objects.equals(entities, that.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entities);
    }

    @Override
    public String toString() {
        return "DisjointRole{" +
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

        constraint.put(KEY_NAME, name);
        constraint.put(KEY_ENTITIES, entities);

        return constraint;
    }
}
