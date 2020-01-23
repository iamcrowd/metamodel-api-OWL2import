package com.gilia.builder.jsontranslator;

import com.gilia.exceptions.EntityNotValidException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;

/**
 * Represents a concrete builder in the Builder design pattern.
 * This class is in charge of building the Metamodel instance according to an UML JSON.
 *
 * @author Emiliano Rios Gavagnin
 */
public class UMLTranslator implements JSONTranslator {


    @Override
    public Metamodel createMetamodel(JSONObject json) {
        Metamodel newMetamodel = new Metamodel();
        JSONArray jsonClasses = (JSONArray) json.get(KEY_CLASSES);
        JSONArray jsonLinks = (JSONArray) json.get(KEY_LINKS);

        identifyObjectTypes(newMetamodel, jsonClasses);
        identifyRelationships(newMetamodel, jsonLinks);
        identifySubclasses(newMetamodel, jsonLinks);

        return newMetamodel;
    }

    /**
     * Identifies each class from a JSONArray of classes obtained from the UML JSON and generates every object type equivalent to the classes given.
     * After identifying each object type, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param jsonClasses JSONArray with the name of the classes represented within the model.
     */
    private void identifyObjectTypes(Metamodel model, JSONArray jsonClasses) {
        // TODO: Existence check
        ArrayList newObjectsType = new ArrayList();
        for (Object umlClass : jsonClasses) {
            String entityName = (String) ((JSONObject) umlClass).get(KEY_NAME);
            ObjectType newObjectType = new ObjectType(entityName);
            newObjectsType.add(newObjectType);
        }

        model.addEntities(newObjectsType);
    }

    /**
     * Identifies each association from a JSONArray of links obtained from the UML JSON and generates every relationship equivalent to the links given.
     * After identifying each association, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param jsonLinks JSONArray with the objects that represents each link within the model.
     */
    private void identifyRelationships(Metamodel model, JSONArray jsonLinks) {
        // TODO: Check if relationship already exist -> Throw exception if already exist
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newRelationships = new ArrayList();
        ArrayList newRoles = new ArrayList();
        for (Object umlLink : jsonLinks) {
            String type = (String) ((JSONObject) umlLink).get(KEY_TYPE);
            if (type.equals(KEY_ASSOCIATION)) {
                JSONObject association = (JSONObject) umlLink;
                String associationName = (String) association.get(KEY_NAME);

                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray classes = (JSONArray) association.get(KEY_CLASSES); // TODO: Check size of classes
                ArrayList objectsType = new ArrayList();
                for (Object jsonClass : classes) {
                    String className = (String) jsonClass;
                    Entity entityFound = checkEntityExistence(model, className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) {
                            objectsType.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for association " + associationName); // TODO: Modify exception to use constants and parametrized messages
                        }
                    } else {
                        ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        model.addEntity(newObjectType);
                    }
                }

                JSONArray jsonRoles = (JSONArray) association.get(KEY_ROLES); // TODO: Check size of roles
                newRoles = identifyRoles(jsonRoles);

                // JSONArray multiplicity = (JSONArray) association.get("multiplicity"); // TODO: Include multiplicity constraint

                Relationship newRelationship = new Relationship(associationName, objectsType, newRoles);
                newRelationships.add(newRelationship);
            }
        }
        model.addRoles(newRoles);
        model.addRelationships(newRelationships);
    }

    /**
     * Identifies each role from a JSONArray of roles obtained from the UML JSON and generates every role as a Role object.
     *
     * @param roles JSONArray with the objects that represents each role from a relationship.
     *
     * @return An ArrayList of Role objects equivalent to the roles given in the JSONArray
     */
    private ArrayList identifyRoles(JSONArray roles) {
        ArrayList newRoles = new ArrayList();
        for (Object umlRole : roles) {
            String roleName = (String) umlRole;
            Role newRole = new Role(roleName);
            newRoles.add(newRole);
        }

        return newRoles;
    }

    /**
     * Identifies each generalization from a JSONArray of links obtained from the UML JSON and generates every subsumption equivalent to the links given.
     * After identifying each generalization, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param jsonLinks JSONArray with the objects that represents each link within the model.
     */
    private void identifySubclasses(Metamodel model, JSONArray jsonLinks) {
        // TODO: Check if subsumption already exist -> Throw exception if already exist
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newSubsumptions = new ArrayList();
        for (Object umlLink : jsonLinks) {
            String type = (String) ((JSONObject) umlLink).get(KEY_TYPE);
            if (type.equals(KEY_GENERALIZATION)) {
                JSONObject subclass = (JSONObject) umlLink;
                String subclassRelationshipName = (String) subclass.get(KEY_NAME);

                // Check the existence of the parent in the generalization
                ObjectType parent;
                String parentName = (String) subclass.get(KEY_PARENT);
                Entity entityFound = checkEntityExistence(model, parentName);
                if (entityFound != null) {
                    if (entityFound.getClass().equals(ObjectType.class)) {
                        parent = (ObjectType) entityFound;
                    } else {
                        throw new EntityNotValidException("Entity " + parentName + " not valid for generalization " + subclassRelationshipName);
                    }
                } else {
                    parent = new ObjectType(parentName);
                    model.addEntity(parent);
                }


                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray classes = (JSONArray) subclass.get(KEY_CLASSES); // TODO: Check size of classes
                ArrayList objectsType = new ArrayList();
                for (Object jsonClass : classes) {
                    String className = (String) jsonClass;
                    entityFound = checkEntityExistence(model, className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) {
                            objectsType.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for generalization " + subclassRelationshipName);
                        }
                    } else {
                        ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        model.addEntity(newObjectType);
                    }
                }

                Subsumption newSubsumption = new Subsumption(subclassRelationshipName, parent, objectsType);
                newSubsumptions.add(newSubsumption);
            }
        }

        model.addRelationships(newSubsumptions);
    }

    /**
     * Checks if a given entity name is already present within the metamodel given.
     * If the entity exists, then it returns the object of that entity.
     * Otherwise, it returns null.
     *
     * @param metamodel A Metamodel instance where to look for the entity
     * @param entityName An entity name to be looked for
     * @return An EntityType, Relationship, Role or Constraint object of the given entityName if it exists in the metamodel. Otherwise returns null.
     */
    private Entity checkEntityExistence(Metamodel metamodel, String entityName) {
        for (EntityType entity : metamodel.getEntities()) {
            if (entity.getName().equals(entityName)) {
                return entity;
            }
        }

        for (Relationship relationship : metamodel.getRelationships()) {
            if (relationship.getName().equals(entityName)) {
                return relationship;
            }
        }

        for (Role role : metamodel.getRoles()) {
            if (role.getName().equals(entityName)) {
                return role;
            }
        }

        for (Constraint constraint : metamodel.getConstraints()) {
            if (constraint.getName().equals(entityName)) {
                return constraint;
            }
        }

        return null;

    }
}
