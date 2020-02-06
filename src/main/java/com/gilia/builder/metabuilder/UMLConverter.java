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

import static com.gilia.utils.Constants.KEY_CLASSES;
import static com.gilia.utils.Constants.KEY_LINKS;

public class UMLConverter implements MetaBuilder {


    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
        JSONObject jsonUml = new JSONObject();

        ArrayList<EntityType> entities = metamodel.getEntities();
        ArrayList<Relationship> relationships = metamodel.getRelationships();

        // Classes
        JSONArray jsonClasses = new JSONArray();
        for (Object entity : entities) {
            jsonClasses.add(((ObjectType) entity).toUML()); // With more entities implementation, this may change
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
            } else {
                jsonLinks.add(((Relationship) relationship).toUML());
            }
        }

        jsonUml.put(KEY_CLASSES, jsonClasses);
        jsonUml.put(KEY_LINKS, jsonLinks);

        return jsonUml;
    }
}
