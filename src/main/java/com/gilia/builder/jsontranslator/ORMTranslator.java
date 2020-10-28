package com.gilia.builder.jsontranslator;

import com.gilia.enumerates.RelationshipType;
import com.gilia.exceptions.AlreadyExistException;
import com.gilia.exceptions.EntityNotValidException;
import com.gilia.exceptions.InformationNotFoundException;
import com.gilia.exceptions.OperationNotSupportedException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.mandatory.Mandatory;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.attributiveproperty.attribute.MappedTo;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * Represents a concrete builder in the Builder design pattern.
 * This class is in charge of building the Metamodel instance according to an ORM JSON.
 */
public class ORMTranslator implements JSONTranslator {
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
        JSONArray jsonRelationships = (JSONArray) json.get(KEY_RELATIONSHIPS);
        JSONArray jsonConnectors = (JSONArray) json.get(KEY_CONNECTORS); // Connectors may not be the best name for this key

        // The order of this calls is important (at least for now)
        identifyEntityTypes(newMetamodel, jsonEntities);
        identifyRelationships(newMetamodel, jsonRelationships); // Roles and Cardinalities are identified by this method as well
        identifySubclasses(newMetamodel, jsonConnectors);
        identifySubsetConstraints(newMetamodel, jsonConnectors);

        return newMetamodel;
    }

    /**
     * Identifies each class from a JSONArray of classes obtained from the UML JSON and generates every object/value type equivalent to the classes given.
     * After identifying each object/value type, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model        Metamodel instance that will incorporate the object types generated by this method
     * @param jsonEntities JSONArray with the name of the classes represented within the model.
     */
    private void identifyEntityTypes(Metamodel model, JSONArray jsonEntities) {
        if (jsonEntities != null) {
            ArrayList newObjectsType = new ArrayList();
            ArrayList newValuesType = new ArrayList();
            for (Object ormEntity : jsonEntities) {
                String entityName = (String) ((JSONObject) ormEntity).get(KEY_NAME);
                String entityType = (String) ((JSONObject) ormEntity).get(KEY_TYPE);
                String entityRef = (String) ((JSONObject) ormEntity).get(KEY_REF);
                if (entityType.equals(ENTITY_STRING) && model.getEntity(entityName) == null) {
                    ObjectType newObjectType = new ObjectType(entityName);
                    newObjectsType.add(newObjectType);
                } else if (entityType.equals(KEY_VALUE) && model.getEntity(entityName) == null) {
                    ValueType newValueType = new ValueType(entityName);
                    newValuesType.add(newValueType);
                } else if (entityType.equals(ENTITY_REF_MODE_STRING) && model.getEntity(entityName) == null) {
                    ObjectType newObjectType = new ObjectType(entityName);
                    ValueType newValueType = new ValueType(entityRef);
                    newObjectsType.add(newObjectType);
                    newValuesType.add(newValueType);
                } else {
                    throw new AlreadyExistException(ALREADY_EXIST_ENTITY_ERROR);
                }
            }
            model.addEntities(newObjectsType);
            model.addEntities(newValuesType);
        } else {
            throw new InformationNotFoundException(ENTITIES_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    /**
     * @param model
     * @param jsonLinks
     */
    private void identifyRelationships(Metamodel model, JSONArray jsonLinks) {
        ArrayList newRoles;
        if (jsonLinks != null) {
            for (Object ormLink : jsonLinks) {
                String type = (String) ((JSONObject) ormLink).get(KEY_TYPE);
                if (type.equals(BINARY_FACT_TYPE_STRING)) {
                    JSONObject binaryFactType = (JSONObject) ormLink;
                    String binaryFactTypeName = (String) binaryFactType.get(KEY_NAME);
                    if (model.getEntity(binaryFactTypeName) == null) {
                        // Check the existence of the entities involved and add the entities not present in the metamodel
                        JSONArray classes = (JSONArray) binaryFactType.get(KEY_ENTITIES); // TODO: Check size of classes
                        ArrayList entityTypes = new ArrayList();

                        boolean isValueTypeRelationship = false;
                        ValueType valueTypeFound = null;
                        for (Object jsonClass : classes) {
                            String className = (String) jsonClass;
                            Entity entityFound = model.getEntity(className);
                            if (entityFound != null) {
                                if (EntityType.class.isAssignableFrom(entityFound.getClass())) {
                                    if (entityFound.getClass().equals(ValueType.class)) {
                                        isValueTypeRelationship = true;
                                        valueTypeFound = (ValueType) entityFound;
                                    }
                                    entityTypes.add(entityFound);
                                }
                            }
                        }

                        if (isValueTypeRelationship && valueTypeFound != null) {
                            ArrayList domain = new ArrayList();
                            domain.add(valueTypeFound);

                            // Hardcoded due to lack of datatype in json
                            DataType dataType = (DataType) model.getEntity("string");
                            if (dataType == null) {
                                dataType = new DataType("string");
                                model.addEntity(dataType);
                            }

                            MappedTo newMappedToRelationship = new MappedTo(valueTypeFound.getName() + "_mappedTo", domain, dataType);
                            valueTypeFound.setMappedTo(newMappedToRelationship);
                            for (Object entity : entityTypes) {
                                if (entity.getClass().equals(ObjectType.class)) {
                                    valueTypeFound.addDomain((ObjectType) entity);
                                }
                            }

                            Relationship valueTypeRelationship = new Relationship(valueTypeFound.getName() + "_valueTypeRel", entityTypes);
                            valueTypeRelationship.setType(RelationshipType.VALUE_TYPE);
                            for (Object entity : entityTypes) {
                                Role role = new Role(((EntityType) entity).getName() + "_role", (EntityType) entity, valueTypeRelationship, "0..1");
                                model.addConstraint(role.getCardinalityConstraints().get(0));
                                valueTypeRelationship.addRole(role);
                                model.addRole(role);
                            }
                            model.addRelationship(valueTypeRelationship);
                            model.addRelationship(newMappedToRelationship);
                        } else {
                            Relationship newRelationship = new Relationship(binaryFactTypeName, entityTypes);
                            model.addRelationship(newRelationship);
                            newRoles = identifyRoles(model, binaryFactType);
                            newRelationship.addRoles(newRoles);
                            model.addRoles(newRoles);
                        }
                    } else {
                        throw new AlreadyExistException(ALREADY_EXIST_RELATIONSHIP_ERROR);
                    }
                }
            }
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }


    /**
     * Identifies each role from a JSONArray of roles obtained from the ORM JSON and generates every role as a Role object.
     * The Role object requires an entity, a relationship and a cardinality constraint. Therefore, the entity and the relationship
     * must exist already within the metamodel given. The constraint will be created with the information provided by the binary fact type
     * JSONObject.
     *
     * @param model          Metamodel instance that will incorporate the roles and cardinalities constraints generated by this method
     * @param binaryFactType JSONObject that represents the binary fact type related to the roles and cardinalities to be identified
     * @return An ArrayList of Role objects equivalent to the roles given in the JSONArray
     */
    private ArrayList identifyRoles(Metamodel model, JSONObject binaryFactType) throws EntityNotValidException {
        String type = (String) binaryFactType.get(KEY_TYPE);
        if (!type.equals(BINARY_FACT_TYPE_STRING)) {
            throw new EntityNotValidException(BINARY_FACT_TYPE_EXPECTED_ERROR);
        }
        ArrayList newRoles = new ArrayList();
        String binaryFactTypeName = (String) binaryFactType.get(KEY_NAME);
        JSONArray jsonEntities = (JSONArray) binaryFactType.get(KEY_ENTITIES);
        JSONArray jsonCardinalities = (JSONArray) binaryFactType.get(KEY_UNIQUENESS_CONSTRAINT);
        JSONArray jsonMandatory = (JSONArray) binaryFactType.get(KEY_MANDATORY);
        if (jsonEntities != null && jsonCardinalities != null) {
            if (jsonEntities.size() == jsonCardinalities.size()) {
                for (int i = 0; i < jsonEntities.size(); i++) {
                    String entityName = (String) jsonEntities.get(i);
                    String roleName = binaryFactTypeName + ROLE_STRING + i;
                    String cardinality = (String) jsonCardinalities.get(i);
                    boolean isMandatory = jsonMandatory.contains(entityName);

                    // Get the entity related to this new role. It should exist already
                    ObjectType entity = (ObjectType) model.getEntity(entityName);
                    if (entity == null) {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }

                    // Get the relationship related to this new role. It should exist already
                    Relationship relationship = (Relationship) model.getEntity(binaryFactTypeName);
                    if (relationship == null) {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }

                    Mandatory mandatory = null;
                    if (isMandatory) {
                        mandatory = new Mandatory("mandatory" + (model.getConstraints().size() + 1));
                    }

                    // Generate the cardinality constraint associated to the new role
                    // TODO: Check existence first?
                    ObjectTypeCardinality newCardinalityConstraint = new ObjectTypeCardinality(model.getOntologyIRI() + "card" + (model.getConstraints().size() + 1), cardinality);
                    model.addConstraint(newCardinalityConstraint);

                    Role newRole = new Role(roleName, entity, relationship, newCardinalityConstraint, mandatory);
                    if (mandatory != null) {
                        model.addConstraint(mandatory);
                        mandatory.setDeclaredOn(newRole);
                    }
                    newRoles.add(newRole);
                }
            } else {
                throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
            }
        }
        return newRoles;
    }

    /**
     * Identifies each isa from a JSONArray of links obtained from the ORM JSON and generates every subsumption equivalent to the links given.
     * After identifying each ias, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model     Metamodel instance that will incorporate the subsumptions generated by this method
     * @param jsonLinks JSONArray with the objects that represents each link within the model.
     */
    private void identifySubclasses(Metamodel model, JSONArray jsonLinks) {
        // TODO: Check if subsumption already exist -> Throw exception if already exist
        // Identify roles -> Check it does not exist -> By definition it can exist for another relationship
        ArrayList newSubsumptions = new ArrayList();
        for (Object ormLink : jsonLinks) {
            String type = (String) ((JSONObject) ormLink).get(KEY_TYPE);
            if (type.equals(SUBTYPING_STRING)) {
                JSONObject subclass = (JSONObject) ormLink;
                String subclassRelationshipName = (String) subclass.get(KEY_NAME);
                JSONArray constraints = (JSONArray) subclass.get(KEY_SUBTYPING_CONSTRAINT);

                // Check the existence of the parent in the generalization
                Entity parent;
                String parentName = (String) subclass.get(KEY_PARENT);
                Entity entityFound = model.getEntity(parentName);
                if (entityFound != null) {
                    if (entityFound.getClass().equals(ObjectType.class)) {
                        parent = entityFound;
                    } else {
                        throw new EntityNotValidException("Entity " + parentName + " not valid for isa " + subclassRelationshipName);
                    }
                } else {
                    // TODO: The non existence of the entity is an exception or should be created?
                    /*parent = new ObjectType(parentName);
                    model.addEntity(parent);*/
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }

                // Check the existence of the entities involved and add the entities not present in the metamodel
                JSONArray entities = (JSONArray) subclass.get(KEY_ENTITIES); // TODO: Check size of classes
                ArrayList metamodelEntities = new ArrayList();
                for (Object jsonClass : entities) {
                    String className = (String) jsonClass;
                    entityFound = model.getEntity(className);
                    if (entityFound != null) {
                        if (entityFound.getClass().equals(ObjectType.class)) { // TODO: Check this
                            metamodelEntities.add(entityFound);
                        } else {
                            throw new EntityNotValidException("Entity " + className + " not valid for isa " + subclassRelationshipName);
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
                        disjointObjectType = new DisjointObjectType(model.getOntologyIRI() + "dc" + (model.getConstraints().size() + 1), metamodelEntities);
                        model.addConstraint(disjointObjectType);
                    } else if (constraint.equals(UNION_STRING)) {
                        completenessConstraint = new CompletenessConstraint(model.getOntologyIRI() + "cc" + (model.getConstraints().size() + 1), metamodelEntities);
                        model.addConstraint(completenessConstraint);
                    }
                }

                for (Object entity : metamodelEntities) {
                    Subsumption newSubsumption = new Subsumption(subclassRelationshipName + "_" + getAlphaNumericString(RANDOM_STRING_LENGTH), parent, (ObjectType) entity, completenessConstraint, disjointObjectType);
                    newSubsumptions.add(newSubsumption);
                }
            }
        }
        model.addRelationships(newSubsumptions);
    }

    private void identifySubsetConstraints(Metamodel model, JSONArray jsonLinks) {
        for (Object ormLink : jsonLinks) {
            String type = (String) ((JSONObject) ormLink).get(KEY_TYPE);
            if (type.equals(KEY_ROLE_CONSTRAINT)) {
                JSONObject roleConstraint = (JSONObject) ormLink;
                String roleConstraintType = (String) roleConstraint.get(KEY_ROLE_CONSTRAINT);
                if (roleConstraintType.equals(SUBSET_CONSTRAINT)) {

                    String subsetName = (String) roleConstraint.get(KEY_NAME);
                    JSONArray factParents = (JSONArray) roleConstraint.get(KEY_FACT_PARENT);
                    JSONArray factTypes = (JSONArray) roleConstraint.get(KEY_FACT_TYPES);

                    JSONArray factParentsPosition = (JSONArray) roleConstraint.get(KEY_FACT_PARENT_POSITION);
                    JSONArray factTypesPosition = (JSONArray) roleConstraint.get(KEY_FACT_TYPES_POSITION);

                    if (factParents.size() > 1 || factTypes.size() > 1 || factParentsPosition.size() > 1 || factTypesPosition.size() > 1) {
                        throw new OperationNotSupportedException("Multiple subset constrains not supported yet");
                    }

                    for (int i = 0; i < factParents.size(); i++) {
                        Entity parent;
                        Entity child;
                        String factParentString = (String) factParents.get(i);
                        Entity entityFound = model.getEntity(factParentString);
                        String position = (String) factParentsPosition.get(i);
                        if (position.contains(CENTER_STRING)) {
                            if (entityFound.getClass().equals(Relationship.class)) { // If parent center -> Sup Relationship
                                parent = entityFound;
                            } else {
                                throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                            }
                        } else if (position.contains(LEFT_STRING) || position.contains(RIGHT_STRING)) { // If parent left/right -> Sup Role
                            if (entityFound.getClass().equals(Relationship.class)) {
                                List<Role> roles = ((Relationship) entityFound).getRoles();
                                if (position.contains(LEFT_STRING)) {
                                    parent = roles.get(0);
                                } else {
                                    parent = roles.get(1);
                                }
                            } else {
                                throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                            }
                        } else {
                            throw new OperationNotSupportedException(INVALID_OPERATION_ERROR);
                        }

                        for (int j = 0; j < factParents.size(); j++) {
                            String factTypeString = (String) factTypes.get(j);
                            entityFound = model.getEntity(factTypeString);
                            position = (String) factTypesPosition.get(i);
                            if (position.contains(CENTER_STRING)) {  // If child center -> Sub Relationship
                                if (entityFound.getClass().equals(Relationship.class)) {
                                    child = entityFound;
                                } else {
                                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                                }
                            } else if (position.contains(LEFT_STRING) || position.contains(RIGHT_STRING)) { // If child left/right -> Sub Role
                                if (entityFound.getClass().equals(Relationship.class)) {
                                    List<Role> roles = ((Relationship) entityFound).getRoles();
                                    if (position.contains(LEFT_STRING)) {
                                        child = roles.get(0);
                                    } else {
                                        child = roles.get(1);
                                    }
                                } else {
                                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                                }
                            } else {
                                throw new OperationNotSupportedException(INVALID_OPERATION_ERROR);
                            }
                            Subsumption newSubsumption = new Subsumption(subsetName + "_" + getAlphaNumericString(RANDOM_STRING_LENGTH), parent, child);
                            model.addRelationship(newSubsumption);
                        }
                    }
                }
            }
        }
    }

}
