package com.gilia.builder.jsontranslator;

import com.gilia.exceptions.AlreadyExistException;
import com.gilia.exceptions.EntityNotValidException;
import com.gilia.exceptions.InformationNotFoundException;
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
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.relationship.attributiveproperty.attribute.MappedTo;
import com.gilia.metamodel.role.Role;
import com.gilia.builder.jsonparser.MetamodelJSONParser;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gilia.utils.Constants.*;

/**
 * Represents a concrete builder in the Builder design pattern.
 * This class is in charge of building the Metamodel instance according to a Metamodel JSON.
 */
public class MetaTranslator implements JSONTranslator {

    // TODO: Implement a Logger

    private MetamodelJSONParser metamodelJSONParser;
    private Metamodel metamodel = new Metamodel();

    @Override
    public Metamodel createMetamodel(JSONObject json) {
        metamodelJSONParser = new MetamodelJSONParser(json);
        String ontologyIRI = "";
        try {
            ontologyIRI = (String) ((JSONObject) ((JSONObject) json.get(KEY_NAMESPACES)).get(KEY_ONTOLOGY_IRI)).get(KEY_VALUE);
        } catch (NullPointerException | ClassCastException e) {
            System.out.println("WARNING: ontologyIRI was not obtained");
        }

        JSONObject jsonEntityTypes = metamodelJSONParser.getMetamodelJSONEntityTypes();

        JSONObject jsonRelationships = (JSONObject) json.get(StringUtils.capitalize(RELATIONSHIP_STRING));
        JSONArray jsonBinaryRelationships = (JSONArray) jsonRelationships.get(StringUtils.capitalize(RELATIONSHIP_STRING));
        JSONArray jsonSubsumptions = (JSONArray) jsonRelationships.get(StringUtils.capitalize(SUBSUMPTION_STRING));
        JSONObject jsonAttributiveProperties = (JSONObject) jsonRelationships.get(StringUtils.capitalize(ATTRIBUTIVE_PROPERTY_STRING));
        JSONArray innerJsonAttributiveProperties = (JSONArray) jsonAttributiveProperties.get(StringUtils.capitalize(ATTRIBUTIVE_PROPERTY_STRING));
        JSONObject jsonAttributes = (JSONObject) jsonAttributiveProperties.get(StringUtils.capitalize(KEY_ATTRIBUTE));
        JSONArray jsonMappedTo = (JSONArray) jsonAttributes.get(StringUtils.capitalize(KEY_MAPPED_TO));

        JSONArray jsonRoles = (JSONArray) json.get(StringUtils.capitalize(ROLE_STRING));
        JSONObject jsonConstraints = (JSONObject) json.get(StringUtils.capitalize(KEY_CONSTRAINTS));

        // The order of this calls is important (at least for now)
        identifyEntityTypesAndAddToMetamodel(jsonEntityTypes);


        identifyRelationships(metamodel, jsonBinaryRelationships); // Does not include subsumptions, roles, nor constraints
        identifyConstraints(metamodel, jsonConstraints);
        identifyRoles(metamodel, jsonRoles);
        identifySubclasses(metamodel, jsonSubsumptions);
        identifyAttributes(metamodel, innerJsonAttributiveProperties);
        identifyMappedTo(jsonMappedTo);

        return metamodel;
    }

    /**
     * Identifies each entity from a JSONObject of entities obtained from the Metamodel JSON and generates every entity type (and specializations) given.
     * After identifying an entity type, it generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param jsonEntityTypes JSONObject with the name of the entities represented within the model.
     */
    private void identifyEntityTypesAndAddToMetamodel(JSONObject jsonEntityTypes) {
        if (jsonEntityTypes != null) {
            JSONArray jsonObjectTypes = metamodelJSONParser.getMetamodelJSONObjectTypes();
            JSONArray jsonDataTypes = metamodelJSONParser.getMetamodelJSONDataTypes();
            JSONArray jsonValueTypes = metamodelJSONParser.getMetamodelJSONValueTypes();

            // TODO: This 3 functions do pretty much the same thing. Maybe change for a factory
            identifyObjectTypesAndAddToMetamodel(jsonObjectTypes);
            identifyDataTypesAndAddToMetamodel(jsonDataTypes);
            identifyValueTypesAndAddToMetamodel(jsonValueTypes);

        } else {
            throw new InformationNotFoundException(ENTITIES_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifyObjectTypesAndAddToMetamodel(JSONArray jsonObjectTypes) {
        for (Object objectType : jsonObjectTypes) {
            String entityName = (String) objectType;
            if (metamodel.getEntity(entityName) == null) {
                ObjectType newObjectType = new ObjectType(entityName);
                metamodel.addEntity(newObjectType);
            } else {
                throw new AlreadyExistException(String.format(ALREADY_EXIST_ENTITY_ERROR, entityName));
            }
        }
    }

    private void identifyDataTypesAndAddToMetamodel(JSONArray jsonDataTypes) {
        for (Object dataType : jsonDataTypes) {
            String entityName = (String) dataType;
            if (metamodel.getEntity(entityName) == null) {
                DataType newDataType = new DataType(entityName);
                metamodel.addEntity(newDataType);
            } else {
                throw new AlreadyExistException(String.format(ALREADY_EXIST_ENTITY_ERROR, entityName));
            }
        }
    }

    private void identifyValueTypesAndAddToMetamodel(JSONArray jsonValueTypes) {
        for (Object valueType : jsonValueTypes) {
            String entityName = (String) valueType;
            if (metamodel.getEntity(entityName) == null) {
                ValueType newValueType = new ValueType(entityName);
                metamodel.addEntity(newValueType);
            } else {
                throw new AlreadyExistException(String.format(ALREADY_EXIST_ENTITY_ERROR, entityName));
            }
        }
    }

    /**
     * Identifies each relationship from a JSONArray of relationships obtained from the Metamodel JSON and generates every relationship.
     * It generates the corresponding metamodel class and incorporates it to the metamodel instance.
     *
     * @param model             Metamodel instance that will incorporate the relationships generated by this method.
     * @param jsonRelationships JSONArray with the objects that represents each relationship within the model.
     */
    private void identifyRelationships(Metamodel model, JSONArray jsonRelationships) {
        if (jsonRelationships != null) {
            ArrayList newRelationships = new ArrayList();
            for (Object metaRelationship : jsonRelationships) {
                String relationshipName = (String) ((JSONObject) metaRelationship).get(KEY_NAME);
                JSONArray entities = (JSONArray) ((JSONObject) metaRelationship).get(KEY_ENTITIES);
                ArrayList objectTypesInvolved = new ArrayList();
                for (Object entity : entities) {
                    String entityName = (String) entity;
                    EntityType entityInvolved = (EntityType) model.getEntity(entityName);
                    if (entityInvolved != null) {
                        objectTypesInvolved.add(entityInvolved);
                    } else {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }
                Relationship newRelationship = new Relationship(relationshipName, objectTypesInvolved);
                newRelationships.add(newRelationship);
            }
            model.addRelationships(newRelationships);
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifyConstraints(Metamodel model, JSONObject jsonConstraints) {
        if (jsonConstraints != null) {
            // Completeness
            JSONArray jsonCompletenessConstraints = (JSONArray) jsonConstraints.get(StringUtils.capitalize(KEY_COMPLETENESS_CONSTRAINT));
            identifyCompletenessConstraints(model, jsonCompletenessConstraints);

            // Cardinality
            JSONObject jsonCardinalityConstraints = (JSONObject) jsonConstraints.get(StringUtils.capitalize(KEY_CARDINALITY_CONSTRAINTS));
            JSONArray jsonObjectTypeCardinalityConstraints = (JSONArray) jsonCardinalityConstraints.get(StringUtils.capitalize(KEY_OBJECT_TYPE_CARDINALITY));
            identifyObjectTypeCardinalityConstraints(model, jsonObjectTypeCardinalityConstraints);

            // Disjointness
            JSONObject jsonDisjointnessConstraints = (JSONObject) jsonConstraints.get(StringUtils.capitalize(KEY_DISJOINTNESS_CONSTRAINT));
            JSONArray jsonDisjointObjectTypeConstraints = (JSONArray) jsonDisjointnessConstraints.get(StringUtils.capitalize(KEY_DISJOINT_OBJECT_TYPE_CONSTRAINT));
            identifyDisjointObjectTypeConstraints(model, jsonDisjointObjectTypeConstraints);

            // Mandatory
            JSONObject jsonMandatoryConstraints = (JSONObject) jsonConstraints.get(StringUtils.capitalize(KEY_MANDATORY_CONSTRAINTS));
            JSONArray jsonMandatory = (JSONArray) jsonMandatoryConstraints.get(StringUtils.capitalize(KEY_MANDATORY));
            identifyMandatory(model, jsonMandatory);
        } else {
            throw new InformationNotFoundException(CONSTRAINTS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifyMandatory(Metamodel model, JSONArray jsonMandatory) {
        if (model != null && jsonMandatory != null) {
            for (Object mandatory : jsonMandatory) {
                JSONObject mandatoryConstraint = (JSONObject) mandatory;
                Mandatory newMandatory = new Mandatory((String) mandatoryConstraint.get(KEY_NAME));
                model.addConstraint(newMandatory);
            }
        }
    }

    private void identifyObjectTypeCardinalityConstraints(Metamodel model, JSONArray jsonCardinalities) {
        if (model != null && jsonCardinalities != null) {
            for (Object constraint : jsonCardinalities) {
                JSONObject cardinalityConstraint = (JSONObject) constraint;
                String constraintName = (String) cardinalityConstraint.get(KEY_NAME);
                String minimum = (String) cardinalityConstraint.get(KEY_MINIMUM);
                String maximum = (String) cardinalityConstraint.get(KEY_MAXIMUM);
                ObjectTypeCardinality newCardinality = new ObjectTypeCardinality(constraintName, minimum, maximum);
                model.addConstraint(newCardinality);
            }
        }
    }

    private void identifyCompletenessConstraints(Metamodel model, JSONArray jsonConstraints) {
        if (model != null && jsonConstraints != null) {
            for (Object constraint : jsonConstraints) {
                JSONObject completenessConstraint = (JSONObject) constraint;
                String constraintName = (String) completenessConstraint.get(KEY_NAME);
                JSONArray entities = (JSONArray) completenessConstraint.get(KEY_ENTITIES);
                ArrayList objectTypesInvolved = new ArrayList();
                for (Object entity : entities) {
                    String entityName = (String) entity;
                    ObjectType objectType = (ObjectType) model.getEntity(entityName);
                    if (objectType != null) {
                        objectTypesInvolved.add(objectType);
                    } else {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }
                CompletenessConstraint newConstraint = new CompletenessConstraint(constraintName, objectTypesInvolved);
                model.addConstraint(newConstraint);
            }
        }
    }

    private void identifyDisjointObjectTypeConstraints(Metamodel model, JSONArray jsonConstraints) {
        if (model != null && jsonConstraints != null) {
            for (Object constraint : jsonConstraints) {
                JSONObject disjointnessConstraint = (JSONObject) constraint;
                String constraintName = (String) disjointnessConstraint.get(KEY_NAME);
                JSONArray entities = (JSONArray) disjointnessConstraint.get(KEY_ENTITIES);
                ArrayList objectTypesInvolved = new ArrayList();
                for (Object entity : entities) {
                    String entityName = (String) entity;
                    ObjectType objectType = (ObjectType) model.getEntity(entityName);
                    if (objectType != null) {
                        objectTypesInvolved.add(objectType);
                    } else {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }
                DisjointObjectType newConstraint = new DisjointObjectType(constraintName, objectTypesInvolved);
                model.addConstraint(newConstraint);
            }
        }
    }

    private void identifyRoles(Metamodel model, JSONArray jsonRoles) {
        if (model != null && jsonRoles != null) {
            for (Object role : jsonRoles) {
                JSONObject jsonRole = (JSONObject) role;
                String roleName = (String) jsonRole.get(KEY_ROLENAME);
                String relationshipName = (String) jsonRole.get(RELATIONSHIP_STRING);
                String entityName = (String) jsonRole.get(KEY_ENTITY_TYPE);
                JSONArray cardinalities = (JSONArray) jsonRole.get(KEY_OBJECT_TYPE_CARDINALITY);
                String mandatory = (String) jsonRole.get(KEY_MANDATORY);

                Relationship relationshipInvolved = (Relationship) model.getEntity(relationshipName);
                EntityType entityInvolved = (EntityType) model.getEntity(entityName);

                if (relationshipInvolved == null || entityInvolved == null) {
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }

                ArrayList cardinalitiesInvolved = new ArrayList();
                for (Object cardinality : cardinalities) {
                    String cardinalityName = (String) cardinality;
                    ObjectTypeCardinality cardinalityInvolved = (ObjectTypeCardinality) model.getEntity(cardinalityName);
                    if (cardinalityInvolved != null) {
                        cardinalitiesInvolved.add(cardinalityInvolved);
                    } else {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }

                Mandatory mandatoryInvolved = null;
                if (mandatory != null) {
                    mandatoryInvolved = (Mandatory) model.getEntity(mandatory);
                }
                Role newRole = new Role(roleName, entityInvolved, relationshipInvolved, cardinalitiesInvolved, mandatoryInvolved);
                relationshipInvolved.addRole(newRole);
                if (mandatory != null && mandatoryInvolved != null) {
                    mandatoryInvolved.setDeclaredOn(newRole);
                }
                model.addRole(newRole);
            }
        } else {
            throw new InformationNotFoundException(ROLES_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifySubclasses(Metamodel model, JSONArray jsonSubsumptions) {
        if (model != null && jsonSubsumptions != null) {
            for (Object metaSubsumption : jsonSubsumptions) {
                String subsumptionName = (String) ((JSONObject) metaSubsumption).get(KEY_NAME);
                String parentName = (String) ((JSONObject) metaSubsumption).get(KEY_ENTITY_PARENT);
                String childName = (String) ((JSONObject) metaSubsumption).get(KEY_ENTITY_CHILD);
                String disjointnessName = (String) ((JSONObject) metaSubsumption).get(KEY_DISJOINTNESS_CONSTRAINT);
                String completenessName = (String) ((JSONObject) metaSubsumption).get(KEY_COMPLETENESS_CONSTRAINT);

                Entity parentInvolved = model.getEntity(parentName);
                Entity childInvolved = model.getEntity(childName);

                if (parentInvolved == null || childInvolved == null) {
                    throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                }

                ArrayList constraintsInvolved = new ArrayList();
                if (disjointnessName != null) {
                    DisjointObjectType disjointnessConstraint = (DisjointObjectType) model.getEntity(disjointnessName);
                    if (disjointnessConstraint == null) {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                    constraintsInvolved.add(disjointnessConstraint);
                }
                if (completenessName != null) {
                    CompletenessConstraint completenessConstraint = (CompletenessConstraint) model.getEntity(completenessName);
                    if (completenessConstraint == null) {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                    constraintsInvolved.add(completenessConstraint);
                }

                Subsumption newSubsumption = new Subsumption(subsumptionName, parentInvolved, childInvolved, constraintsInvolved);
                model.addRelationship(newSubsumption);
            }
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifyAttributes(Metamodel model, JSONArray jsonAttributes) {
        if (jsonAttributes != null) {
            for (Object metaAttributiveProperty : jsonAttributes) {
                String attributivePropertyName = (String) ((JSONObject) metaAttributiveProperty).get(KEY_NAME);
                String range = (String) ((JSONObject) metaAttributiveProperty).get(KEY_RANGE);
                JSONArray domain = (JSONArray) ((JSONObject) metaAttributiveProperty).get(KEY_DOMAIN);
                ArrayList entitiesInvolved = new ArrayList();
                for (Object entity : domain) {
                    String entityName = (String) entity;
                    Entity entityInvolved = (Entity) model.getEntity(entityName);
                    if (entityInvolved != null) {
                        entitiesInvolved.add(entityInvolved);
                    } else {
                        throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
                    }
                }

                Entity dataType = model.getEntity(range);
                if (dataType == null) {
                    dataType = new DataType(range);
                    model.addEntity((EntityType) dataType);
                } else if (dataType.getClass() != DataType.class) {
                    throw new EntityNotValidException("");
                }

                AttributiveProperty attributiveProperty = new AttributiveProperty(attributivePropertyName, entitiesInvolved, (DataType) dataType);
                model.addRelationship(attributiveProperty);
            }
        } else {
            throw new InformationNotFoundException(RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR);
        }
    }

    private void identifyMappedTo(JSONArray jsonArrayMappedTo) {
        if (jsonArrayMappedTo != null) {
            for (Object jsonMappedTo : jsonArrayMappedTo) {
                JSONObject mappedTo = (JSONObject) jsonMappedTo;

                JSONArray jsonDomains = (JSONArray) mappedTo.get(KEY_DOMAIN);
                List<Entity> domains = new ArrayList<>();
                for (Object domainName : jsonDomains) {
                    domains.add((ValueType) metamodel.getEntity((String) domainName));
                }

                String mappedToName = (String) mappedTo.get(KEY_NAME);
                String range = (String) mappedTo.get(KEY_RANGE);
                DataType dataType = (DataType) metamodel.getEntityType(range);
                MappedTo newMappedTo = new MappedTo(mappedToName, domains, dataType);

                for (Entity domain : domains) {
                    ((ValueType) domain).setMappedTo(newMappedTo);
                }
                metamodel.addRelationship(newMappedTo);
            }
        }
    }

}
