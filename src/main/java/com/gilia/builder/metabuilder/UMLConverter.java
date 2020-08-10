package com.gilia.builder.metabuilder;

import com.gilia.enumerates.RelationshipType;
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
import com.gilia.metamodel.relationship.attributiveproperty.attribute.MappedTo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;

public class UMLConverter implements MetaBuilder {

    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
        JSONObject jsonUml = new JSONObject();

        ArrayList<EntityType> entities = (ArrayList<EntityType>) metamodel.getEntities();
        ArrayList<Relationship> relationships = (ArrayList<Relationship>) metamodel.getRelationships();


        // Classes
        JSONArray jsonClasses = new JSONArray();
        for (Object entity : entities) {
            if (entity.getClass() == ObjectType.class) {
                JSONObject json = ((ObjectType) entity).toUML();
                JSONArray attributes = new JSONArray();
                for (Object relationship : relationships) {
                    if (relationship.getClass() == AttributiveProperty.class) {
                        if (((AttributiveProperty) relationship).getDomain().contains(entity)) {
                            attributes.add(((AttributiveProperty) relationship).toUML());
                        }
                    } else if (relationship.getClass() == MappedTo.class) {
                        for (Entity objectType : ((MappedTo) relationship).getDomain()) {
                            ValueType valueType = (ValueType) objectType;
                            if (valueType.getDomain().contains(entity)) {
                                AttributiveProperty attributiveProperty = (valueType).toAttributiveProperty();
                                attributes.add((attributiveProperty).toUML());
                            }
                        }

                    }
                }
                json.replace(KEY_ATTRS, attributes);
                jsonClasses.add(json); // With more entities implementation, this may change
            }
        }

        // Links
        JSONArray jsonLinks = new JSONArray();
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
                        JSONObject jsonSubsumption = subsumption.toUML();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_CLASSES, jsonEntitiesInvolved);
                        jsonLinks.add(jsonSubsumption);
                        constraintsEvaluated.add(completenessConstraint);
                    } else if (disjointObjectType != null) {
                        /* For each Disjointness constraint that is declared on two or more Object types,
                        these Object types share a direct common subsumer */
                        ArrayList<ObjectType> entitiesInvolved = disjointObjectType.getEntities();
                        JSONObject jsonSubsumption = subsumption.toUML();
                        JSONArray jsonEntitiesInvolved = new JSONArray();
                        entitiesInvolved.forEach(entity -> jsonEntitiesInvolved.add(entity.getName()));
                        jsonSubsumption.put(KEY_CLASSES, jsonEntitiesInvolved);
                        jsonLinks.add(jsonSubsumption);
                        constraintsEvaluated.add(disjointObjectType);
                    } else {
                        jsonLinks.add(subsumption.toUML());
                    }
                }
            } else if (relationship.getClass() == Relationship.class) {
                if (((Relationship) relationship).getType() == null || !((Relationship) relationship).getType().equals(RelationshipType.VALUE_TYPE)) {
                    jsonLinks.add(((Relationship) relationship).toUML());
                }
            }
        }

        jsonUml.put(KEY_CLASSES, jsonClasses);
        jsonUml.put(KEY_LINKS, jsonLinks);

        return jsonUml;
    }
}
