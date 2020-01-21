package com.gilia.metastrategies;

import com.gilia.exceptions.EntityNotValidException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * @author Emiliano Rios Gavagnin
 */
public class UmlToMeta extends Metastrategy {

    /**
     *
     */
    public UmlToMeta() {
        super();
    }

    /**
     * Creates a Metamodel instance based on a given UML model. The JSONObject should be a valid JSON according to the JSON Schema that defines UML JSONs.
     *
     * @param umlModel A valid JSONObject that represents an UML model.
     * @return A JSONObject that describes the Metamodel generated from the given UML
     */
    @Override
    public JSONObject createModel(JSONObject umlModel) {

        JSONArray jsonClasses = (JSONArray) umlModel.get("classes");
        JSONArray jsonLinks = (JSONArray) umlModel.get("links");

        identifyClasses(jsonClasses);
        identifyAssociations(jsonLinks);
        identifySubclasses(jsonLinks);

        return model.toJSONObject();
    }

    /**
     * Identifies each class from a JSONArray of classes obtained from the UML JSON and generates every object type equivalent to the classes given.
     *
     * @param jsonClasses
     */
    private ArrayList<EntityType> identifyClasses(JSONArray jsonClasses) {
        // TODO: Check if already exist -> Throw exception if already exist
        ArrayList newObjectsType = new ArrayList();
        for (Object umlClass : jsonClasses) {
            String entityName = (String) ((JSONObject) umlClass).get("name");
            ObjectType newObjectType = new ObjectType(entityName);
            newObjectsType.add(newObjectType);
        }

        this.model.addEntities(newObjectsType);
        return newObjectsType;
    }

    /**
     * @param jsonLinks
     */
    private ArrayList identifyAssociations(JSONArray jsonLinks) {
        // Identify relationships -> Check if already exist -> Throw exception if already exist
        // Identify entities -> If already exist, then reuse
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newRelationships = new ArrayList();
        for (Object umlLink : jsonLinks) {
            String type = (String) ((JSONObject) umlLink).get("type");
            if (type.equals("association")) {
                JSONObject association = (JSONObject) umlLink;
                String associationName = (String) association.get("name");

                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray classes = (JSONArray) association.get("classes"); // TODO: Check size of classes
                ArrayList objectsType = new ArrayList();
                for (Object jsonClass : classes) {
                    String className = (String) jsonClass;
                    Entity entityFound = checkEntityExistence(className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) {
                            objectsType.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for association " + associationName);
                        }
                    } else {
                        ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        this.model.addEntity(newObjectType);
                    }
                }

                JSONArray jsonRoles = (JSONArray) association.get("roles"); // TODO: Check size of roles
                ArrayList roles = identifyRoles(jsonRoles);

                // JSONArray multiplicity = (JSONArray) association.get("multiplicity"); // TODO: Include multiplicity constraint

                Relationship newRelationship = new Relationship(associationName, objectsType, roles);
                newRelationships.add(newRelationship);
            }
        }

        this.model.addRelationships(newRelationships);
        return newRelationships;
    }

    private ArrayList identifyRoles(JSONArray roles) {
        ArrayList newRoles = new ArrayList();
        for (Object umlRole : roles) {
            String roleName = (String) umlRole;
            Role newRole = new Role(roleName);
            newRoles.add(newRole);
        }

        this.model.addRoles(newRoles);
        return newRoles;
    }

    /**
     * @param jsonLinks
     */
    private ArrayList identifySubclasses(JSONArray jsonLinks) {
        // Identify relationships -> Check if already exist -> Throw exception if already exist
        // Identify entities -> If already exist, then reuse
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newSubsumptions = new ArrayList();
        for (Object umlLink : jsonLinks) {
            String type = (String) ((JSONObject) umlLink).get("type");
            if (type.equals("generalization")) {
                JSONObject subclass = (JSONObject) umlLink;
                String subclassRelationshipName = (String) subclass.get("name");

                // Check the existence of the parent in the generalization
                ObjectType parent;
                String parentName = (String) subclass.get("parent");
                Entity entityFound = checkEntityExistence(parentName);
                if (entityFound != null) {
                    if (entityFound.getClass().equals(ObjectType.class)) {
                        parent = (ObjectType) entityFound;
                    } else {
                        throw new EntityNotValidException("Entity " + parentName + " not valid for generalization " + subclassRelationshipName);
                    }
                } else {
                    parent = new ObjectType(parentName);
                    this.model.addEntity((ObjectType) parent);
                }


                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray classes = (JSONArray) subclass.get("classes"); // TODO: Check size of classes
                ArrayList objectsType = new ArrayList();
                for (Object jsonClass : classes) {
                    String className = (String) jsonClass;
                    entityFound = checkEntityExistence(className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) {
                            objectsType.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for generalization " + subclassRelationshipName);
                        }
                    } else {
                        ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        this.model.addEntity(newObjectType);
                    }
                }

                Subsumption newSubsumption = new Subsumption(subclassRelationshipName, parent, objectsType);
                newSubsumptions.add(newSubsumption);
            }
        }

        this.model.addRelationships(newSubsumptions);
        return newSubsumptions;
    }

    private Entity checkEntityExistence(String entityName) {
        for (EntityType entity : model.getEntities()) {
            if (entity.getName().equals(entityName)) {
                return entity;
            }
        }

        for (Relationship relationship : model.getRelationships()) {
            if (relationship.getName().equals(entityName)) {
                return relationship;
            }
        }

        for (Role role : model.getRoles()) {
            if (role.getName().equals(entityName)) {
                return role;
            }
        }

        for (Constraint constraint : model.getConstraints()) {
            if (constraint.getName().equals(entityName)) {
                return constraint;
            }
        }

        return null;

    }


}
