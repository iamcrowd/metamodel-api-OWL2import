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

    public ArrayList<EntityType> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<EntityType> entities) {
        this.entities = entities;
    }

    public ArrayList<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(ArrayList<Relationship> relationships) {
        this.relationships = relationships;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(ArrayList<Constraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * Adds a single EntityType object to the Metamodel instance
     *
     * @param entity EntityType object to be added
     */
    public void addEntity(EntityType entity) {
        this.entities.add(entity);
    }

    /**
     * Adds a collection of entities (EntityType objects) to the Metamodel instance
     *
     * @param entities ArrayList of EntityType objects to be added
     */
    public void addEntities(ArrayList<EntityType> entities) {
        this.entities.addAll(entities);
    }

    /**
     * Adds a single Relationship object to the Metamodel instance
     *
     * @param relationship Relationship object to be added
     */
    public void addRelationship(Relationship relationship) {
        this.relationships.add(relationship);
    }

    /**
     * Adds a collection of relationships (Relationship objects) to the Metamodel instance
     *
     * @param relationships ArrayList of Relationship objects to be added
     */
    public void addRelationships(ArrayList<Relationship> relationships) {
        this.relationships.addAll(relationships);
    }

    /**
     * Adds a single Role object to the Metamodel instance
     *
     * @param role Role object to be added
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Adds a collection of roles (Role objects) to the Metamodel instance
     *
     * @param roles ArrayList of Role objects to be added
     */
    public void addRoles(ArrayList<Role> roles) {
        this.roles.addAll(roles);
    }

    /**
     * Adds a single Constraint object to the Metamodel instance
     *
     * @param constraint Constraint object to be added
     */
    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    /**
     * Search for an entity (EntityType object) in the Metamodel according to its name. Returns a new empty ObjectType
     * object if there is no EntityType object with the given name.
     *
     * @param name String that represents the entity name
     * @return EntityType object that has the given name or a new ObjectType object if there is no match
     */
    public EntityType getEntity(String name) {
        for (EntityType entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return new ObjectType();
    }

    /**
     * Search for a relationship (Relationship object) in the Metamodel according to its name. Returns a new empty Relationship
     * object if there is no Relationship object with the given name.
     *
     * @param name String that represents the relationship name
     * @return Relationship object that has the given name or a new Relationship object if there is no match
     */
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

    @Override
    public int hashCode() {
        return Objects.hash(entities, relationships, roles, constraints);
    }

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
        // TODO: Implement? This is already done by MetaConverter/generateJSON
        return new JSONObject();
    }
}
