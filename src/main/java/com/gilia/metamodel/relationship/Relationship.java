package com.gilia.metamodel.relationship;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Relationship extends Entity {
    protected ArrayList<ObjectType> entities;
    protected ArrayList<Role> roles;

    public Relationship(){
        super();
        this.entities = new ArrayList();
        this.roles = new ArrayList();
    }

    /**
     *
     * @param name
     */
    public Relationship(String name) {
        super(name);
        this.entities = new ArrayList();
        this.roles = new ArrayList();
    }

    public Relationship(String name, ArrayList<ObjectType> entities){
        super(name);
        this.entities = entities;
        this.roles = new ArrayList();;
    }

    /**
     *
     * @param name
     * @param entities
     * @param roles
     */
    public Relationship(String name, ArrayList<ObjectType> entities, ArrayList<Role> roles) {
        super(name);
        this.entities = entities;
        this.roles = roles;
    }

    /**
     *
     * @return
     */
    public ArrayList<ObjectType> getEntities() {
        return entities;
    }

    /**
     *
     * @param entities
     */
    public void setEntities(ArrayList<ObjectType> entities) {
        this.entities = entities;
    }

    /**
     *
     * @return
     */
    public ArrayList<Role> getRoles() {
        return roles;
    }

    /**
     *
     * @param roles
     */
    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Relationship that = (Relationship) o;
        return Objects.equals(entities, that.entities) &&
                Objects.equals(roles, that.roles);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entities, roles);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Relationship{" +
                "entities=" + entities +
                ", roles=" + roles +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     *
     * @return
     */
    public JSONObject toJSONObject() {
        JSONObject relationship = new JSONObject();
        JSONArray entitiesJSON = new JSONArray();

        for (Object entity : entities){
            entitiesJSON.add(((ObjectType) entity).getName());
        }

        relationship.put("name", name);
        relationship.put("entities", entitiesJSON);
        return relationship;
    }
}
