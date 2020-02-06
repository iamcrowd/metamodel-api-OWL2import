package com.gilia.builder.jsontranslator;

import com.gilia.exceptions.AlreadyExistException;
import com.gilia.exceptions.EntityNotValidException;
import com.gilia.exceptions.InconsistentModelInformationException;
import com.gilia.exceptions.InformationNotFoundException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * Represents a concrete builder in the Builder design pattern.
 * This class is in charge of building the Metamodel instance according to an EER JSON.
 */
public class EERTranslator implements JSONTranslator {
    @Override
    public Metamodel createMetamodel(JSONObject json) {
        String ontologyIRI = "";
        try {
            ontologyIRI = (String) ((JSONObject) ((JSONObject) json.get(KEY_NAMESPACES)).get(KEY_ONTOLOGY_IRI)).get(KEY_VALUE);
        } catch (NullPointerException | ClassCastException e) {
            System.out.println("WARNING: ontologyIRI was not obtained");
        }

        Metamodel newMetamodel = new Metamodel(ontologyIRI);
        JSONArray jsonEntities = (JSONArray) json.get(KEY_ENTITIES);
        // JSONArray jsonAttributes = (JSONArray) json.get(KEY_ATTRIBUTES); // This is not a 1:1 Mapping
        //JSONArray jsonRelationships = (JSONArray) json.get(KEY_RELATIONSHIPS);
        JSONArray jsonLinks = (JSONArray) json.get(KEY_LINKS);

        // The order of this calls is important (at least for now)
        identifyObjectTypes(newMetamodel, jsonEntities);
        identifyRelationships(newMetamodel, jsonLinks); // TODO: Implement identifyRelationships
        identifySubclasses(newMetamodel, jsonLinks); // TODO: Implement identifySubclasses

        return newMetamodel;
    }

    /**
     * Identifies each entity from a JSONArray of entities obtained from the EER JSON and generates every object type equivalent to the entites given.
     * After identifying each object type, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model        Metamodel instance that will incorporate the object types generated by this method
     * @param jsonEntities JSONArray with the name of the entities represented within the model.
     */
    private void identifyObjectTypes(Metamodel model, JSONArray jsonEntities) {

        if (jsonEntities != null) {
            ArrayList newObjectsType = new ArrayList();
            for (Object eerEntity : jsonEntities) {
                String entityName = (String) ((JSONObject) eerEntity).get(KEY_NAME);
                if (model.checkEntityExistence(entityName) == null) {
                    ObjectType newObjectType = new ObjectType(entityName);
                    newObjectsType.add(newObjectType);
                } else {
                    throw new AlreadyExistException(ALREADY_EXIST_ENTITY_ERROR);
                }
            }
            model.addEntities(newObjectsType);
        } else {
            throw new InformationNotFoundException(ENTITIES_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    /**
     * Identifies each relationship from a JSONArray of relationships/links obtained from the EER JSON and generates every relationship equivalent to the links given.
     * After identifying each relationship, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model     Metamodel instance that will incorporate the relationships, roles and cardinalities constraints generated by this method
     * @param jsonLinks JSONArray with the objects that represents each relationship/link within the model.
     */
    private void identifyRelationships(Metamodel model, JSONArray jsonLinks) {
        // TODO: Check if relationship already exist -> Throw exception if already exist
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newRelationships = new ArrayList();
        ArrayList newRoles = new ArrayList();
        ArrayList newCardinalitiesConstraints = new ArrayList();
        if (jsonLinks != null) {
            for (Object eerLink : jsonLinks) {
                String type = (String) ((JSONObject) eerLink).get(KEY_TYPE);
                if (type.equals(RELATIONSHIP_STRING)) {
                    JSONObject relationship = (JSONObject) eerLink;
                    String relationshipName = (String) relationship.get(KEY_NAME);
                    if (model.checkEntityExistence(relationshipName) == null) {
                        // Check the existence of the entities involved and add the entities not present in the metamodel
                        JSONArray entities = (JSONArray) relationship.get(KEY_ENTITIES); // TODO: Check size of classes
                        ArrayList objectsType = new ArrayList();
                        for (Object jsonEntity : entities) {
                            String entityName = (String) jsonEntity;
                            Entity entityFound = model.checkEntityExistence(entityName);
                            if (entityFound != null) {
                                if (entityFound.getClass().equals(ObjectType.class)) {
                                    objectsType.add(entityFound);
                                } else {
                                    throw new EntityNotValidException("Entity " + entityName + " not valid for relationship " + relationshipName); // TODO: Modify exception to use constants and parametrized messages
                                }
                            } else {
                                // TODO: The non existence of the entity is an exception or should be created?
                                        /*ObjectType newObjectType = new ObjectType(className);
                                        objectsType.add(newObjectType);
                                        model.addEntity(newObjectType);*/
                                throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                            }
                        }

                        Relationship newRelationship = new Relationship(relationshipName, objectsType);
                        model.addRelationship(newRelationship); // TODO: The relationship is not saving the roles
                        newRoles = identifyRoles(model, relationship);
                    } else {
                        throw new AlreadyExistException(ALREADY_EXIST_RELATIONSHIP_ERROR);
                    }
                    model.addRoles(newRoles);
                }
            }
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    /**
     * Identifies each role from a JSONArray of roles obtained from the UML JSON and generates every role as a Role object.
     * The Role object requires an entity, a relationship and a cardinality constraint. Therefore, the entity and the relationship
     * must exist already within the metamodel given. The constraint will be created with the information provided by the association
     * JSONObject.
     *
     * @param model            Metamodel instance that will incorporate the roles and cardinalities constraints generated by this method
     * @param jsonRelationship JSONObject that represents the relationship related to the roles and cardinalities to be identified
     * @return An ArrayList of Role objects equivalent to the roles given in the JSONArray
     */
    private ArrayList identifyRoles(Metamodel model, JSONObject jsonRelationship) throws EntityNotValidException {

        String type = (String) jsonRelationship.get(KEY_TYPE);
        if (!type.equals(RELATIONSHIP_STRING)) {
            throw new EntityNotValidException(RELATIONSHIP_EXPECTED_ERROR);
        }

        ArrayList newRoles = new ArrayList();
        String associationName = (String) jsonRelationship.get(KEY_NAME);
        JSONArray jsonEntities = (JSONArray) jsonRelationship.get(KEY_ENTITIES);
        JSONArray jsonRoles = (JSONArray) jsonRelationship.get(KEY_ROLES);
        JSONArray jsonCardinalities = (JSONArray) jsonRelationship.get(KEY_CARDINALITY);
        if (jsonEntities != null && jsonRoles != null && jsonCardinalities != null) {
            if (jsonRoles.size() == jsonCardinalities.size()) {
                for (int i = 0; i < jsonRoles.size(); i++) {
                    String entityName = (String) jsonEntities.get(i);
                    String roleName = (String) jsonRoles.get(i);
                    String cardinality = (String) jsonCardinalities.get(i);

                    // Get the entity related to this new role. It should exist already
                    ObjectType entity = (ObjectType) model.getEntity(entityName);
                    // TODO: Change this to checkEntityExistence
                    if (entity.isNameless()) { // If the entity returned is nameless, then it does not exist or is not valid.
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }

                    // Get the relationship related to this new role. It should exist already
                    Relationship relationship = model.getRelationship(associationName);
                    // TODO: Change this to checkEntityExistence
                    if (relationship.isNameless()) { // If the relationship returned is nameless, then it does not exist or is not valid.
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }

                    // Generate the cardinality constraint associated to the new role
                    // TODO: Check existence first?
                    ObjectTypeCardinality newCardinalityConstraint = new ObjectTypeCardinality(model.getOntologyIRI() + "card" + (model.getConstraints().size() + 1), cardinality);
                    model.addConstraint(newCardinalityConstraint);

                    Role newRole = new Role(roleName, entity, relationship, newCardinalityConstraint);
                    newRoles.add(newRole);
                }
            } else {
                throw new InconsistentModelInformationException(INCONSISTENT_ROLES_WITH_CARDINALITIES_ERROR);
            }
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
        return newRoles;
    }

    /**
     * Identifies each isa relationship from a JSONArray of links obtained from the UML JSON and generates every subsumption equivalent to the links given.
     * After identifying each isa, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model     Metamodel instance that will incorporate the subsumptions generated by this method
     * @param jsonLinks JSONArray with the objects that represents each link within the model.
     */
    private void identifySubclasses(Metamodel model, JSONArray jsonLinks) {
        // TODO: Check if subsumption already exist -> Throw exception if already exist
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newSubsumptions = new ArrayList();
        for (Object eerLink : jsonLinks) {
            String type = (String) ((JSONObject) eerLink).get(KEY_TYPE);
            if (type.equals(ISA_STRING)) {
                JSONObject isa = (JSONObject) eerLink;
                String isaRelationshipName = (String) isa.get(KEY_NAME);
                JSONArray constraints = (JSONArray) isa.get(KEY_CONSTRAINT);

                // Check the existence of the parent in the generalization
                ObjectType parent;
                String parentName = (String) isa.get(KEY_PARENT); // TODO: Check case of "undefined"
                Entity entityFound = model.checkEntityExistence(parentName);
                if (entityFound != null) {
                    if (entityFound.getClass().equals(ObjectType.class)) {
                        parent = (ObjectType) entityFound;
                    } else {
                        throw new EntityNotValidException("Entity " + parentName + " not valid for isa relationship " + isaRelationshipName);
                    }
                } else {
                    // TODO: The non existence of the entity is an exception or should be created?
                    /*parent = new ObjectType(parentName);
                    model.addEntity(parent);*/
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }


                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray classes = (JSONArray) isa.get(KEY_ENTITIES); // TODO: Check size of classes
                ArrayList objectsType = new ArrayList();
                for (Object jsonClass : classes) {
                    String className = (String) jsonClass;
                    entityFound = model.checkEntityExistence(className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) {
                            objectsType.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for generalization " + isaRelationshipName);
                        }
                    } else {
                        // TODO: The non existence of the entity is an exception or should be created?
                        /*ObjectType newObjectType = new ObjectType(className);
                        objectsType.add(newObjectType);
                        model.addEntity(newObjectType);*/
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }

                DisjointObjectType disjointObjectType = null;
                CompletenessConstraint completenessConstraint = null;
                for (Object o : constraints) {
                    String constraint = (String) o;
                    if (constraint.equals(EXCLUSIVE_STRING)) {
                        disjointObjectType = new DisjointObjectType(model.getOntologyIRI() + "dc" + (model.getConstraints().size() + 1), objectsType);
                        model.addConstraint(disjointObjectType);
                    } else if (constraint.equals(UNION_STRING)) {
                        completenessConstraint = new CompletenessConstraint(model.getOntologyIRI() + "cc" + (model.getConstraints().size() + 1), objectsType);
                        model.addConstraint(completenessConstraint);
                    }
                }

                for (Object entity : objectsType){
                    Subsumption newSubsumption = new Subsumption(isaRelationshipName + "_" + getAlphaNumericString(RANDOM_STRING_LENGTH), parent, (ObjectType) entity, completenessConstraint, disjointObjectType);
                    newSubsumptions.add(newSubsumption);
                }
            }
        }
        model.addRelationships(newSubsumptions);
    }

}