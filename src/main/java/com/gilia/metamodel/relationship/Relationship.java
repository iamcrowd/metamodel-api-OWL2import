package com.gilia.metamodel.relationship;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.KEY_ENTITIES;
import static com.gilia.utils.Constants.KEY_NAME;

/**
 * Representation of the Relationship class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Relationship extends Entity {
    protected ArrayList<ObjectType> entities;
    protected ArrayList<Role> roles;

    /**
     * Creates a basic instance of a Relationship. It will be created without information. The only information generated will be an id.
     */
    public Relationship() {
        super();
        this.entities = new ArrayList();
        this.roles = new ArrayList();
    }

    /**
     * Creates a basic instance of a Relationship. It will be created only with the name of the relationship. Any other information will be missing.
     *
     * @param name String that represents the name of the relationship
     */
    public Relationship(String name) {
        super(name);
        this.entities = new ArrayList();
        this.roles = new ArrayList();
    }

    /**
     * Creates an instance of a Relationship. This constructor receives information about the name of the relationship,
     * and the entities involved in the relationship. By definition, a Relationship must have at least two roles.
     *
     * @param name     String that represents the name of the relationship
     * @param entities ArrayList of ObjectType that represents the entities involved in the relationship
     */
    public Relationship(String name, ArrayList<ObjectType> entities) {
        super(name);
        this.entities = entities;
        this.roles = new ArrayList();
    }

    /**
     * Creates an instance of a Relationship. This constructor receives information about the name of the relationship, the roles of the relationship,
     * and the entities involved in the relationship
     *
     * @param name     String that represents the name of the relationship
     * @param entities ArrayList of ObjectType that represents the entities involved in the relationship
     * @param roles    ArrayList of Role that represents the roles involved in the relationship
     */
    public Relationship(String name, ArrayList<ObjectType> entities, ArrayList<Role> roles) {
        super(name);
        this.entities = entities;
        this.roles = roles;
    }

    public ArrayList<ObjectType> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<ObjectType> entities) {
        this.entities = entities;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Relationship that = (Relationship) o;
        return Objects.equals(entities, that.entities) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entities, roles);
    }

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
     * Creates a JSONObject with the relevant information about the Relationship object. The JSON format is based on the
     * Metamodel JSON Schema.
     * Example:
     * { <p>
     * "entities": [ <p>
     * "http://crowd.fi.uncoma.edu.ar#Class1", <p>
     * "http://crowd.fi.uncoma.edu.ar#Class2" <p>
     * ], <p>
     * "name": "http://crowd.fi.uncoma.edu.ar#Rel1" <p>
     * }
     *
     * @return JSONObject with information about the Relationship object
     */
    public JSONObject toJSONObject() {
        JSONObject relationship = new JSONObject();
        JSONArray entitiesJSON = new JSONArray();

        for (Object entity : entities) {
            entitiesJSON.add(((ObjectType) entity).getName());
        }

        relationship.put(KEY_NAME, name);
        relationship.put(KEY_ENTITIES, entitiesJSON);
        return relationship;
    }
}
