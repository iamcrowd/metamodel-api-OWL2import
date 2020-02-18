package com.gilia.builder.metabuilder;

import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;

public class EERConverter implements MetaBuilder {
    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
        JSONObject jsonEer = new JSONObject();

        ArrayList<EntityType> entities = metamodel.getEntities();
        ArrayList<Relationship> relationships = metamodel.getRelationships();

        // Entities
        JSONArray jsonEntities = new JSONArray();
        for (Object entity : entities) {
            jsonEntities.add(((ObjectType) entity).toEER()); // With more entities implementation, this may change
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
            } else {
                jsonLinks.add(((Relationship) relationship).toEER());
            }
        }

        jsonEer.put(KEY_ENTITIES, jsonEntities);
        jsonEer.put(KEY_ATTRIBUTES, new JSONArray());
        jsonEer.put(KEY_RELATIONSHIPS, new JSONArray());
        jsonEer.put(KEY_LINKS, jsonLinks);

        return jsonEer;
    }
}
