package com.gilia.builder.metabuilder;

import com.gilia.enumerates.RelationshipType;
import com.gilia.exceptions.EntityNotValidException;
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

public class EERConverter implements MetaBuilder {
    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
        JSONObject jsonEer = new JSONObject();

        ArrayList<EntityType> entities = (ArrayList<EntityType>) metamodel.getEntities();
        ArrayList<Relationship> relationships = (ArrayList<Relationship>) metamodel.getRelationships();

        // Entities
        JSONArray jsonEntities = new JSONArray();
        for (Object entity : entities) {
            if (entity.getClass() == ObjectType.class) {
                jsonEntities.add(((ObjectType) entity).toEER()); // With more entities implementation, this may change
            }
        }

        // Links
        JSONArray jsonLinks = new JSONArray();
        JSONArray jsonAttributes = new JSONArray();
        ArrayList<Constraint> constraintsEvaluated = new ArrayList();
        for (Object relationship : relationships) {
            if (relationship.getClass() == Subsumption.class) {
                Subsumption subsumption = (Subsumption) relationship;
                CompletenessConstraint completenessConstraint = subsumption.getCompleteness();
                DisjointObjectType disjointObjectType = subsumption.getDisjointness();
                if (!constraintsEvaluated.contains(completenessConstraint) && !constraintsEvaluated.contains(disjointObjectType)) {
                    if (completenessConstraint != null) {
                        ArrayList<ObjectType> entitiesInvolved = completenessConstraint.getEntities();
                        JSONObject jsonSubsumption = subsumption.toEER();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_ENTITIES, jsonEntitiesInvolved);
                        jsonLinks.add(jsonSubsumption);
                        constraintsEvaluated.add(completenessConstraint);
                    } else if (disjointObjectType != null) {
                        ArrayList<ObjectType> entitiesInvolved = disjointObjectType.getEntities();
                        JSONObject jsonSubsumption = subsumption.toEER();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_ENTITIES, jsonEntitiesInvolved);
                        jsonLinks.add(jsonSubsumption);
                        constraintsEvaluated.add(disjointObjectType);
                    } else {
                        jsonLinks.add(subsumption.toEER());
                    }
                }
            } else if (relationship.getClass() == AttributiveProperty.class) { // TODO: Add attributes from value types
                jsonLinks.addAll(((AttributiveProperty) relationship).toEERLinks());
                jsonAttributes.addAll(((AttributiveProperty) relationship).toEERAttributes());
            } else if (relationship.getClass() == Relationship.class) {
                Relationship relation = ((Relationship) relationship);
                if (relation.getType() == RelationshipType.VALUE_TYPE) {
                    List<Entity> relationEntities = relation.getEntities();
                    Entity firstEntity = relationEntities.get(0);
                    Entity secondEntity = relationEntities.get(1);
                    if (firstEntity.getClass() == ValueType.class) {
                        AttributiveProperty attribute = ((ValueType) firstEntity).toAttributiveProperty();
                        jsonLinks.addAll(attribute.toEERLinks());
                        jsonAttributes.addAll(attribute.toEERAttributes());
                    } else if (secondEntity.getClass() == ValueType.class) {
                        AttributiveProperty attribute = ((ValueType) secondEntity).toAttributiveProperty();
                        jsonLinks.addAll(attribute.toEERLinks());
                        jsonAttributes.addAll(attribute.toEERAttributes());
                    }
                } else {
                    jsonLinks.add(((Relationship) relationship).toEER());
                }
            }
        }

        jsonEer.put(KEY_ENTITIES, jsonEntities);
        jsonEer.put(KEY_ATTRIBUTES, jsonAttributes);
        jsonEer.put(KEY_RELATIONSHIPS, new JSONArray()); // TODO: This is left empty for now due to redundancy. Maybe a new JSON Schema should be considered
        jsonEer.put(KEY_LINKS, jsonLinks);

        return jsonEer;
    }
}
