package com.gilia.builder.metabuilder;

import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.mandatory.Mandatory;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueProperty;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class MetaConverter implements MetaBuilder {
    @Override
    public JSONObject generateJSON(Metamodel metamodel) {

        JSONObject jsonMetamodel = new JSONObject();

        ArrayList entities = metamodel.getEntities();
        ArrayList relationships = metamodel.getRelationships();
        ArrayList roles = metamodel.getRoles();
        ArrayList constraints = metamodel.getConstraints();


        // Entity type
        JSONArray objectTypes = new JSONArray();
        JSONArray dataTypes = new JSONArray();
        JSONArray valueProperties = new JSONArray();

        for (Object entity : entities) {
            if (ObjectType.class.equals(entity.getClass())) {
                objectTypes.add(((ObjectType) entity).getName());
            } else if (DataType.class.equals(entity.getClass())) {
                dataTypes.add(((DataType) entity).getName());
            } else if (ValueProperty.class.equals(entity.getClass())) {
                valueProperties.add(((ValueProperty) entity).getName());
            }
        }

        JSONObject entityType = new JSONObject();

        entityType.put("Object type", objectTypes);
        entityType.put("Data type", dataTypes);
        entityType.put("Value property", valueProperties);

        jsonMetamodel.put("Entity type", entityType);


        // Relationship
        JSONArray subsumptionsJSONArray = new JSONArray();
        JSONArray relationshipsJSONArray = new JSONArray();
        JSONArray attributePropertiesJSONArray = new JSONArray();

        for (Object relation : relationships) {
            if (Subsumption.class.equals(relation.getClass())) {
                subsumptionsJSONArray.add(((Subsumption) relation).toJSONObject());
            } else if (AttributiveProperty.class.equals(relation.getClass())) {
                // TODO: Implement class
            } else if (Relationship.class.equals(relation.getClass())) {
                relationshipsJSONArray.add(((Relationship) relation).toJSONObject());
            }
        }

        JSONObject relationship = new JSONObject();

        relationship.put("Subsumption", subsumptionsJSONArray);
        relationship.put("Relationship", relationshipsJSONArray);
        relationship.put("Attributive Property", attributePropertiesJSONArray);

        jsonMetamodel.put("Relationship", relationship);


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
