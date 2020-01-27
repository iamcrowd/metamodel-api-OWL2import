package com.gilia.metamodel;

import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Representation of a Metamodel instance. This class encapsulates all the entities, relationships, roles and constraints generated from a UML/EER/ORM model.
 *
 * @author Emiliano Rios Gavagnin
 */
public class Metamodel {

    private String ontologyIRI;
    private ArrayList<EntityType> entities;
    private ArrayList<Relationship> relationships;
    private ArrayList<Role> roles;
    private ArrayList<Constraint> constraints;

    /**
     *
     */
    public Metamodel() {
        this.ontologyIRI = "";
        this.entities = new ArrayList();
        this.relationships = new ArrayList();
        this.roles = new ArrayList();
        this.constraints = new ArrayList();
    }

    /**
     *
     */
    public Metamodel(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
        this.entities = new ArrayList();
        this.relationships = new ArrayList();
        this.roles = new ArrayList();
        this.constraints = new ArrayList();
    }


    public String getOntologyIRI() {
        return ontologyIRI;
    }

    public void setOntologyIRI(String ontologyIRI) {
        this.ontologyIRI = ontologyIRI;
    }

    /**
     * @return
     */
    public ArrayList<EntityType> getEntities() {
        return entities;
    }

    /**
     * @param entities
     */
    public void setEntities(ArrayList<EntityType> entities) {
        this.entities = entities;
    }

    /**
     * @return
     */
    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * @param relationships
     */
    public void setRelationships(ArrayList<Relationship> relationships) {
        this.relationships = relationships;
    }

    /**
     * @return
     */
    public ArrayList<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles
     */
    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    /**
     * @return
     */
    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints
     */
    public void setConstraints(ArrayList<Constraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * @param entity
     */
    public void addEntity(EntityType entity) {
        this.entities.add(entity);
    }

    /**
     * @param entities
     */
    public void addEntities(ArrayList<EntityType> entities) {
        this.entities.addAll(entities);
    }

    /**
     * @param relationship
     */
    public void addRelationship(Relationship relationship) {
        this.relationships.add(relationship);
    }

    /**
     * @param relationships
     */
    public void addRelationships(ArrayList<Relationship> relationships) {
        this.relationships.addAll(relationships);
    }

    /**
     * @param role
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * @param roles
     */
    public void addRoles(ArrayList<Role> roles) {
        this.roles.addAll(roles);
    }

    /**
     * @param constraint
     */
    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public EntityType getEntity(String name) {
        for (EntityType entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return new ObjectType();
    }

    public Relationship getRelationship(String name) {
        for (Relationship relationship : relationships) {
            if (relationship.getName().equals(name)) {
                return relationship;
            }
        }
        return new Relationship();
    }

    /**
     * Checks if a given entity name is already present within the metamodel given.
     * If the entity exists, then it returns the object of that entity.
     * Otherwise, it returns null.
     *
     * @param entityName An entity name to be looked for
     * @return An EntityType, Relationship, Role or Constraint object of the given entityName if it exists in the metamodel. Otherwise returns null.
     */
    public Entity checkEntityExistence(String entityName) {
        for (EntityType entity : entities) {
            if (entity.getName().equals(entityName)) {
                return entity;
            }
        }

        for (Relationship relationship : relationships) {
            if (relationship.getName().equals(entityName)) {
                return relationship;
            }
        }

        for (Role role : roles) {
            if (role.getName().equals(entityName)) {
                return role;
            }
        }

        for (Constraint constraint : constraints) {
            if (constraint.getName().equals(entityName)) {
                return constraint;
            }
        }

        return null;
    }


    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Metamodel metamodel = (Metamodel) obj;
        return Objects.equals(entities, metamodel.entities) &&
                Objects.equals(relationships, metamodel.relationships) &&
                Objects.equals(roles, metamodel.roles) &&
                Objects.equals(constraints, metamodel.constraints);
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(entities, relationships, roles, constraints);
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "Metamodel{" +
                "entities=" + entities +
                ", relationships=" + relationships +
                ", roles=" + roles +
                ", constraints=" + constraints +
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
