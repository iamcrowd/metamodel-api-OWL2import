package com.gilia.builder.metabuilder;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.gilia.utils.Constants.*;

public class ORMConverter implements MetaBuilder {
    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
        JSONObject jsonOrm = new JSONObject();

        ArrayList<EntityType> entities = metamodel.getEntities();
        ArrayList<Relationship> relationships = metamodel.getRelationships();

        // Entities
        JSONArray jsonEntities = new JSONArray();
        for (Object entity : entities) {
            if (entity.getClass() == ObjectType.class) {
                jsonEntities.add(((ObjectType) entity).toORM()); // With more entities implementation, this may change
            }
        }

        // Relationships
        JSONArray jsonRelationships = new JSONArray();
        JSONArray jsonConnectors = new JSONArray();
        ArrayList<Constraint> constraintsEvaluated = new ArrayList();
        for (Object relationship : relationships) {
            if (relationship.getClass() == Subsumption.class) {
                Subsumption subsumption = (Subsumption) relationship;
                CompletenessConstraint completenessConstraint = subsumption.getCompleteness();
                DisjointObjectType disjointObjectType = subsumption.getDisjointness();
                if (!constraintsEvaluated.contains(completenessConstraint) && !constraintsEvaluated.contains(disjointObjectType)) {
                    if (completenessConstraint != null) {
                        /* For each Completeness constraint that is declared on two or more Object types,
                        these Object types share a direct common subsumer */
                        ArrayList<ObjectType> entitiesInvolved = completenessConstraint.getEntities();
                        JSONObject jsonSubsumption = subsumption.toORM();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_ENTITIES, jsonEntitiesInvolved);
                        jsonConnectors.add(jsonSubsumption);
                        constraintsEvaluated.add(completenessConstraint);
                    } else if (disjointObjectType != null) {
                        /* For each Disjointness constraint that is declared on two or more Object types,
                        these Object types share a direct common subsumer */
                        ArrayList<ObjectType> entitiesInvolved = disjointObjectType.getEntities();
                        JSONObject jsonSubsumption = subsumption.toORM();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_ENTITIES, jsonEntitiesInvolved);
                        jsonConnectors.add(jsonSubsumption);
                        constraintsEvaluated.add(disjointObjectType);
                    } else {
                        jsonConnectors.add(subsumption.toORM());
                    }
                }
            } else if (relationship.getClass() == AttributiveProperty.class) { // Attributes -> Value Types
                AttributiveProperty attributiveProperty = (AttributiveProperty) relationship;
                List<Entity> generatedEntities = attributiveProperty.toValueType();
                for (Entity entity : generatedEntities) {
                    if (entity.getClass() == ValueType.class) {
                        jsonEntities.add(((ValueType) entity).toORM());
                    } else if (entity.getClass() == Relationship.class) {
                        jsonRelationships.add(((Relationship) entity).toORM());
                    }
                }
            } else {
                jsonRelationships.add(((Relationship) relationship).toORM());
            }
        }

        jsonOrm.put(KEY_ENTITIES, jsonEntities);
        jsonOrm.put(KEY_RELATIONSHIPS, jsonRelationships);
        jsonOrm.put(KEY_CONNECTORS, jsonConnectors);
        jsonOrm.put(KEY_ATTRIBUTES, new JSONArray());
        jsonOrm.put(KEY_INHERITANCES, new JSONArray());

        return jsonOrm;
    }
}
