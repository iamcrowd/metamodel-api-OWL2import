package com.gilia.metamodel.relationship;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Relationship extends Entity { // TODO: 1:1 Mapping
    // TODO: Study more about Relationships formalization (KF metamodel formalization - Page 8)
    protected ArrayList<String> entities;
    protected ArrayList<Role> roles;

    /**
     *
     * @param name
     */
    public Relationship(String name) {
        super(name);
        this.entities = new ArrayList();
        this.roles = new ArrayList();
    }

    /**
     *
     * @param name
     * @param entities
     * @param roles
     */
    public Relationship(String name, ArrayList entities, ArrayList roles) {
        super(name);
        this.entities = entities;
        this.roles = roles;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getEntities() {
        return entities;
    }

    /**
     *
     * @param entities
     */
    public void setEntities(ArrayList<String> entities) {
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
        // TODO: Implement
        return new JSONObject();
    }
}
