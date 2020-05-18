package com.gilia.builder.metabuilder;

import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.mandatory.Mandatory;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueProperty;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.relationship.attributiveproperty.attribute.Attribute;
import com.gilia.metamodel.relationship.attributiveproperty.attribute.MappedTo;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class MetaConverter implements MetaBuilder {
    @Override
    public JSONObject generateJSON(Metamodel metamodel) {

        JSONObject jsonMetamodel = new JSONObject();

        ArrayList entities = (ArrayList) metamodel.getEntities();
        ArrayList relationships = (ArrayList) metamodel.getRelationships();
        ArrayList roles = (ArrayList) metamodel.getRoles();
        ArrayList constraints = (ArrayList) metamodel.getConstraints();


        // Entity type
        JSONObject entityType = new JSONObject();
        JSONArray objectTypes = new JSONArray();
        JSONArray dataTypes = new JSONArray();
        JSONObject valueProperties = new JSONObject();
        JSONArray valueTypes = new JSONArray();

        valueProperties.put("Value type", valueTypes);
        entityType.put("Object type", objectTypes);
        entityType.put("Data type", dataTypes);
        entityType.put("Value property", valueProperties);
        jsonMetamodel.put("Entity type", entityType);

        for (Object entity : entities) {
            if (ObjectType.class.equals(entity.getClass())) {
                objectTypes.add(((ObjectType) entity).getName());
            } else if (DataType.class.equals(entity.getClass())) {
                dataTypes.add(((DataType) entity).getName());
            } else if (ValueProperty.class.isAssignableFrom(entity.getClass())) {
                if (ValueType.class.equals(entity.getClass())) {
                    valueTypes.add(((ValueType) entity).getName());
                }
            }
        }

        // Relationship
        JSONObject relationship = new JSONObject();
        JSONArray subsumptionsJSONArray = new JSONArray();
        JSONArray relationshipsJSONArray = new JSONArray();
        JSONObject attributePropertiesJSONObject = new JSONObject();
        JSONArray attributePropertiesJSONArray = new JSONArray();
        JSONObject attributeJSONObject = new JSONObject();
        JSONArray attributeJSONArray = new JSONArray();
        JSONArray mappedToJSONArray = new JSONArray();

        relationship.put("Subsumption", subsumptionsJSONArray);
        relationship.put("Relationship", relationshipsJSONArray);
        relationship.put("Attributive property", attributePropertiesJSONObject);
        attributePropertiesJSONObject.put("Attributive property", attributePropertiesJSONArray);
        attributePropertiesJSONObject.put("Attribute", attributeJSONObject);
        attributeJSONObject.put("Attribute", attributeJSONArray);
        attributeJSONObject.put("Mapped to", mappedToJSONArray);
        jsonMetamodel.put("Relationship", relationship);

        for (Object relation : relationships) {
            if (Subsumption.class.equals(relation.getClass())) {
                subsumptionsJSONArray.add(((Subsumption) relation).toJSONObject());
            } else if (AttributiveProperty.class.isAssignableFrom(relation.getClass())) {
                if (AttributiveProperty.class.equals(relation.getClass())) {
                    attributePropertiesJSONArray.add(((AttributiveProperty) relation).toJSONObject());
                } else if (Attribute.class.equals(relation.getClass())) {
                    attributeJSONArray.add(((Attribute) relation).toJSONObject());
                } else if (MappedTo.class.equals(relation.getClass())) {
                    mappedToJSONArray.add(((MappedTo) relation).toJSONObject());
                }
            } else if (Relationship.class.equals(relation.getClass())) {
                relationshipsJSONArray.add(((Relationship) relation).toJSONObject());
            }
        }


        // Role
        JSONArray rolesJSONArray = new JSONArray();
        for (Object role : roles) {
            rolesJSONArray.add(((Role) role).toJSONObject());
        }
        jsonMetamodel.put("Role", rolesJSONArray);


        // Constraint
        JSONObject jsonConstraints = new JSONObject();
        JSONObject disjointnessConstraints = new JSONObject();
        JSONObject completenessConstraints = new JSONObject();
        JSONObject cardinalityConstraints = new JSONObject();
        JSONObject mandatoryConstraints = new JSONObject();

        // Get ObjectType cardinalities constraints
        JSONArray objectTypeCardinalitiesJSONArray = new JSONArray();
        for (Object constraint : constraints) {
            if (ObjectTypeCardinality.class.equals(constraint.getClass())) {
                objectTypeCardinalitiesJSONArray.add(((ObjectTypeCardinality) constraint).toJSONObject());
            }
        }

        cardinalityConstraints.put("Object type cardinality", objectTypeCardinalitiesJSONArray);
        cardinalityConstraints.put("Attibutive property cardinality", new JSONArray());
        jsonConstraints.put("Cardinality constraints", cardinalityConstraints);

        // Get Disjointness constraints
        JSONArray disjointnessConstraintsJSONArray = new JSONArray();
        for (Object constraint : constraints) {
            if (DisjointObjectType.class.equals(constraint.getClass())) {
                disjointnessConstraintsJSONArray.add(((DisjointObjectType) constraint).toJSONObject());
            }
        }

        disjointnessConstraints.put("Disjoint object type", disjointnessConstraintsJSONArray);
        disjointnessConstraints.put("Disjoint role", new JSONArray());
        jsonConstraints.put("Disjointness constraints", disjointnessConstraints);


        // Get Completeness constraints
        JSONArray completenessConstraintsJSONArray = new JSONArray();
        for (Object constraint : constraints) {
            if (CompletenessConstraint.class.equals(constraint.getClass())) {
                completenessConstraintsJSONArray.add(((CompletenessConstraint) constraint).toJSONObject());
            }
        }

        jsonConstraints.put("Completeness constraints", completenessConstraintsJSONArray);

        // Get Mandatory constraints
        JSONArray mandatoryConstraintsJSONArray = new JSONArray();
        for (Object constraint : constraints) {
            if (Mandatory.class.equals(constraint.getClass())) {
                mandatoryConstraintsJSONArray.add(((Mandatory) constraint).toJSONObject());
            }
        }

        mandatoryConstraints.put("Mandatory", mandatoryConstraintsJSONArray);
        jsonConstraints.put("Mandatory constraints", mandatoryConstraints);


        jsonMetamodel.put("Constraints", jsonConstraints);

        return jsonMetamodel;

    }
}
