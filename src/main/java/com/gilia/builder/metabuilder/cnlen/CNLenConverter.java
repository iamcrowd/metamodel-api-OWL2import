package com.gilia.builder.metabuilder.cnlen;

import com.gilia.builder.metabuilder.*;
import com.gilia.enumerates.RelationshipType;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.Metamodel;
import com.gilia.metamodel.role.Role;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.constraint.mandatory.Mandatory;
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

public class CNLenConverter implements MetaBuilder {

    @Override
    public JSONObject generateJSON(Metamodel metamodel) {
    	JSONArray arrayCNL = new JSONArray();
        JSONObject jsonCNLen = new JSONObject();

        ArrayList<EntityType> entities = (ArrayList<EntityType>) metamodel.getEntities();
        ArrayList<Relationship> relationships = (ArrayList<Relationship>) metamodel.getRelationships();
        ArrayList<Role> roles = (ArrayList<Role>) metamodel.getRoles();        
        ArrayList<Constraint> constraints = (ArrayList<Constraint>) metamodel.getConstraints();

        for (EntityType entity : entities) {
            entity.toCNLen();
            arrayCNL.add(entity.getCNLen());
        }
        
        ArrayList<Constraint> constraintsEvaluated = new ArrayList();
        
        for (Relationship relationship : relationships) {
            if (relationship.getClass() == Subsumption.class) {
                Subsumption subsumption = (Subsumption) relationship;
                subsumption.toCNLen();
                arrayCNL.add(subsumption.getCNLen());

                CompletenessConstraint completenessConstraint = subsumption.getCompleteness();
                DisjointObjectType disjointObjectType = subsumption.getDisjointness();
                
                if (!constraintsEvaluated.contains(completenessConstraint)){
                    if (completenessConstraint != null){
                        completenessConstraint.toCNLen(subsumption.getParent().getName());
                        arrayCNL.add(completenessConstraint.getCNLen());
                        constraintsEvaluated.add(completenessConstraint);
                    }
                }    

                if (!constraintsEvaluated.contains(disjointObjectType)){
                    if (disjointObjectType != null){
                        disjointObjectType.toCNLen(subsumption.getParent().getName());
                        arrayCNL.add(disjointObjectType.getCNLen());
                        constraintsEvaluated.add(disjointObjectType);
                    }
                }
            }
            else if (relationship.getClass() == AttributiveProperty.class) {
                relationship.toCNLen();
                arrayCNL.add(((AttributiveProperty)relationship).getCNLen());
                arrayCNL.add(((AttributiveProperty)relationship).getCNLen_attrProp());
            }
            else if (relationship.getClass() == Relationship.class){
                    relationship.toCNLen();
        	        arrayCNL.add(relationship.getCNLen());
            }
        }

        for (Role role : roles) {
        	role.toCNLen();
        	arrayCNL.add(role.getCNLen());
            arrayCNL.add(role.getCNLen_card());
            
            if (role.getMandatoryConstraint() != null){
                role.getMandatoryConstraint().toCNLen();
                arrayCNL.add(role.getMandatoryConstraint().getCNLen());
            }
        }

        jsonCNLen.put("CNLen", arrayCNL);
        return jsonCNLen;
    }
}
