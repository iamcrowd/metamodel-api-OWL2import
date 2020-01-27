package com.gilia.builder.jsontranslator;

import com.gilia.exceptions.EntityNotValidException;
import com.gilia.exceptions.InconsistentModelInformationException;
import com.gilia.exceptions.InformationNotFoundException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
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
        String ontologyIRI = (String) ((JSONObject) ((JSONObject) json.get(KEY_NAMESPACES)).get(KEY_ONTOLOGY_IRI)).get(KEY_VALUE);

        Metamodel newMetamodel = new Metamodel(ontologyIRI);
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
        if (jsonClasses != null) {
            for (Object umlClass : jsonClasses) {
                String entityName = (String) ((JSONObject) umlClass).get(KEY_NAME);
                ObjectType newObjectType = new ObjectType(entityName);
                newObjectsType.add(newObjectType);
            }

            model.addEntities(newObjectsType);
        } else {
            throw new InformationNotFoundException(ENTITIES_INFORMATION_NOT_FOUND_ERROR);
        }
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
        ArrayList newCardinalitiesConstraints = new ArrayList();
        if (jsonLinks != null) {
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
                        Entity entityFound = model.checkEntityExistence(className);
                        if (entityFound != null) {
                            if (entityFound.getClass().equals(ObjectType.class)) {
                                objectsType.add(entityFound);
                            } else {
                                throw new EntityNotValidException("Entity " + className + " not valid for association " + associationName); // TODO: Modify exception to use constants and parametrized messages
                            }
                        } else {
                            // TODO: The non existence of the entity is an exception or should be created?
                        /*ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        model.addEntity(newObjectType);*/
                            throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                        }
                    }

                    Relationship newRelationship = new Relationship(associationName, objectsType);
                    model.addRelationship(newRelationship);
                    newRoles = identifyRoles(model, association);
                }
            }
            model.addRoles(newRoles);
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    /**
     * Identifies each role from a JSONArray of roles obtained from the UML JSON and generates every role as a Role object.
     *
     * @param model
     * @param association
     * @return An ArrayList of Role objects equivalent to the roles given in the JSONArray
     */
    private ArrayList identifyRoles(Metamodel model, JSONObject association) throws EntityNotValidException {
        ArrayList newRoles = new ArrayList();
        String associationName = (String) association.get(KEY_NAME);
        JSONArray jsonClasses = (JSONArray) association.get(KEY_CLASSES);
        JSONArray jsonRoles = (JSONArray) association.get(KEY_ROLES);
        JSONArray jsonCardinalities = (JSONArray) association.get(KEY_MULTIPLICITY);
        if (jsonRoles.size() == jsonCardinalities.size()) {
            for (int i = 0; i < jsonRoles.size(); i++) {
                String entityName = (String) jsonClasses.get(i);
                String roleName = (String) jsonRoles.get(i);
                String cardinality = (String) jsonCardinalities.get(i);

                // Get the entity related to this new role. It should exist already
                ObjectType entity = (ObjectType) model.getEntity(entityName);
                if (entity.isNameless()) { // If the entity returned is nameless, then it does not exist or is not valid.
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }

                // Get the relationship related to this new role. It should exist already
                Relationship relationship = model.getRelationship(associationName);
                if (relationship.isNameless()) { // If the relationship returned is nameless, then it does not exist or is not valid.
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }

                // Generate the cardinality constraint associated to the new role
                ObjectTypeCardinality newCardinalityConstraint = new ObjectTypeCardinality(model.getOntologyIRI() + "card" + (model.getConstraints().size() + 1), cardinality);
                model.addConstraint(newCardinalityConstraint);

                Role newRole = new Role(roleName, entity, relationship, newCardinalityConstraint);
                newRoles.add(newRole);
            }
        } else {
            throw new InconsistentModelInformationException(INCONSISTENT_ROLES_WITH_CARDINALITIES_ERROR);
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
                Entity entityFound = model.checkEntityExistence(parentName);
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
                    entityFound = model.checkEntityExistence(className);
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

}
