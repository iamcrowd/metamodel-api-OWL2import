package com.gilia.metamodel.relationship;

import com.gilia.exceptions.MetamodelDefinitionCompromisedException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.*;

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

    /**
     * Adds a Role object to the relationship. This method checks that the role
     * does not exist already in the relationship and that the relationship contains
     * a maximum of two roles (including the one to be added).
     *
     * @param role Role object to be added to the relationship
     * @throws MetamodelDefinitionCompromisedException
     */
    public void addRole(Role role) throws MetamodelDefinitionCompromisedException {
        if (roles.size() <= 1 && !roles.contains(role)) {
            roles.add(role);
        } else {
            throw new MetamodelDefinitionCompromisedException(RELATIONSHIP_DEFINITION_ERROR);
        }
    }

    /**
     * Adds a list of Roles object to the relationship. This method checks that the role
     * does not exist already in the relationship and that the relationship contains
     * a maximum of two roles (including the one to be added).
     *
     * @param roles Role object to be added to the relationship
     * @throws MetamodelDefinitionCompromisedException
     */
    public void addRoles(ArrayList<Role> roles) throws MetamodelDefinitionCompromisedException {
        if (roles.size() <= 2 && this.roles.size() == 0) {
            for (Role role : roles) {
                this.roles.add(role);
            }
        } else {
            throw new MetamodelDefinitionCompromisedException(RELATIONSHIP_DEFINITION_ERROR);
        }
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

    /**
     * Generates a JSONObject that represents the information of the Relationship according to the
     * UML language. The JSONObject generated respects the UML Schema.
     *
     * @return JSONObject that represents the equivalent UML Relationship.
     */
    public JSONObject toUML() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, KEY_ASSOCIATION);

        // Classes involved
        JSONArray jsonEntities = new JSONArray();
        this.entities.forEach(entity -> jsonEntities.add(entity.getName()));
        jsonObject.put(KEY_CLASSES, jsonEntities);

        // Roles and cardinalities
        JSONArray jsonRoles = new JSONArray();
        JSONArray jsonMultiplicity = new JSONArray();
        if (roles.size() >= 2) {
            for (Role role : roles) {
                jsonRoles.add(role.getName());
                if ((role.getCardinalityConstraints() != null) && (role.getCardinalityConstraints().size() >= 1)) {
                    jsonMultiplicity.add(role.getCardinalityConstraints().get(0).getCardinality());
                } else {
                    throw new MetamodelDefinitionCompromisedException("Can not generate UML for Relationship " + name + ". Role definition has been violated.");
                }
            }
        } else {
            throw new MetamodelDefinitionCompromisedException("Can not generate UML for Relationship " + name + ". Relationship definition has been violated.");
        }
        jsonObject.put(KEY_ROLES, jsonRoles);

        JSONArray realJsonMultiplicity = new JSONArray();
        for (int i = jsonMultiplicity.size() - 1; i >= 0; i--) {
            realJsonMultiplicity.add(jsonMultiplicity.get(i));
        }
        jsonObject.put(KEY_MULTIPLICITY, realJsonMultiplicity);

        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Relationship according to the
     * ORM language. The JSONObject generated respects the ORM Schema.
     *
     * @return JSONObject that represents the equivalent ORM Binary Fact Type.
     */
    public JSONObject toORM() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, BINARY_FACT_TYPE_STRING);

        // Classes involved
        JSONArray jsonEntities = new JSONArray();
        this.entities.forEach(entity -> jsonEntities.add(entity.getName()));
        jsonObject.put(KEY_ENTITIES, jsonEntities);

        // Roles and cardinalities
        JSONArray jsonMandatory = new JSONArray();
        JSONArray jsonMultiplicity = new JSONArray();
        if (roles.size() >= 2) {
            for (Role role : roles) {
                if ((role.getCardinalityConstraints() != null) && (role.getCardinalityConstraints().size() >= 1)) {
                    for (ObjectTypeCardinality cardinality : role.getCardinalityConstraints()) {
                        jsonMultiplicity.add(cardinality.getCardinality());
                    }
                    if (role.getMandatoryConstraint() != null) {
                        jsonMandatory.add(role.getEntity().getName());
                    }
                } else {
                    throw new MetamodelDefinitionCompromisedException("Can not generate ORM for Relationship " + name + ". Role definition has been violated.");
                }
            }
        } else {
            throw new MetamodelDefinitionCompromisedException("Can not generate ORM for Relationship " + name + ". Relationship definition has been violated.");
        }
        jsonObject.put(KEY_MANDATORY, jsonMandatory);
        jsonObject.put(KEY_UNIQUENESS_CONSTRAINT, jsonMultiplicity);

        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Relationship according to the
     * EER language. The JSONObject generated respects the EER Schema.
     *
     * @return JSONObject that represents the equivalent EER Relationship.
     */
    public JSONObject toEER() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, RELATIONSHIP_STRING);

        // Classes involved
        JSONArray jsonEntities = new JSONArray();
        this.entities.forEach(entity -> jsonEntities.add(entity.getName()));
        jsonObject.put(KEY_ENTITIES, jsonEntities);

        // Roles and cardinalities
        JSONArray jsonRoles = new JSONArray();
        JSONArray jsonCardinalities = new JSONArray();
        if (roles.size() >= 2) {
            for (Role role : roles) {
                if ((role.getCardinalityConstraints() != null) && (role.getCardinalityConstraints().size() >= 1)) {
                    jsonRoles.add(role.getName());
                    for (ObjectTypeCardinality cardinality : role.getCardinalityConstraints()) {
                        jsonCardinalities.add(cardinality.getCardinality());
                    }
                } else {
                    throw new MetamodelDefinitionCompromisedException("Can not generate EER for Relationship " + name + ". Role definition has been violated.");
                }
            }
        } else {
            throw new MetamodelDefinitionCompromisedException("Can not generate EER for Relationship " + name + ". Relationship definition has been violated.");
        }
        jsonObject.put(KEY_ROLES, jsonRoles);
        jsonObject.put(KEY_CARDINALITY, jsonCardinalities);

        return jsonObject;
    }
}
