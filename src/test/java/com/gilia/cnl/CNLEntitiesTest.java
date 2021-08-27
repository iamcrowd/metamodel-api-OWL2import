package com.gilia.cnl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import com.gilia.metamodel.*;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.role.Role;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.mandatory.Mandatory;


import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;



/**
 * mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
 */
public class CNLEntitiesTest {

    @Test
    public void testObjectTypeCNL() {
        try {
        	
        	  ObjectType objectType = new ObjectType("Person");
        	  objectType.toCNLen();
        	  assertEquals("testObjectTypeCNL", objectType.getCNLen(), "Person is an Object type.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    public void testDataTypeCNL() {
        try {
        	
        	  DataType dataType = new DataType("string");
        	  dataType.toCNLen();
        	  assertEquals("testDataTypeCNL", dataType.getCNLen(), "String is a Data type.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testAttributePropertyCNL() {
        try {
        	  List<Entity> entity = new ArrayList<>();
        	  DataType dataType = new DataType("string");
        	  dataType.toCNLen();
        	  assertEquals("testDataTypeCNL", dataType.getCNLen(), "String is a Data type.");
              
        	  ObjectType firstEntity = new ObjectType("FirstEntity");
              entity.add(firstEntity);
              
        	  AttributiveProperty attr = new AttributiveProperty("attr", entity, dataType);
        	  
        	  attr.toCNLen();
        	  System.out.println(attr.getCNLen());
        	  

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testRoleCNL() {
        try {
            String relationshipName = "aRelationship";
            ObjectType firstEntity = new ObjectType("FirstEntity");
            ObjectType secondEntity = new ObjectType("SecondEntity");

            ArrayList entities = new ArrayList();
            entities.add(firstEntity);
            entities.add(secondEntity);

            Relationship newRelationship = new Relationship(relationshipName, entities);
            
            ObjectTypeCardinality card1 = new ObjectTypeCardinality("card1", "2", "3");
            ObjectTypeCardinality card2 = new ObjectTypeCardinality("card2", "0", "*");
           
            
            Role role_a = new Role("aRole1", firstEntity, newRelationship, card1);
            Role role_b = new Role("aRole2", secondEntity, newRelationship, card2);
        	
        	role_a.toCNLen();
        	role_b.toCNLen();
        	assertEquals("testRoleCNL", role_a.getCNLen(), "ARole1 is a role in a relationship aRelationship, each FirstEntity aRole1 s at least 2 SecondEntity and each FirstEntity aRole1 s at most 3 SecondEntity.");
        	assertEquals("testRoleCNL", role_b.getCNLen(), "ARole2 is a role in a relationship aRelationship, each SecondEntity aRole2 s at least 0 FirstEntity and each SecondEntity aRole2 s at most * FirstEntity.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testRelationshipCNL() {
        try {
            String relationshipName = "aRelationship";
            ObjectType firstEntity = new ObjectType("FirstEntity");
            ObjectType secondEntity = new ObjectType("SecondEntity");
            Role role_a = new Role("aRole1");
            Role role_b = new Role("aRole2");
            
            ArrayList roles = new ArrayList();
            ArrayList entities = new ArrayList();
            roles.add(role_a);
            roles.add(role_b);
            entities.add(firstEntity);
            entities.add(secondEntity);

            Relationship newRelationship = new Relationship(relationshipName, entities, roles);
        	
            newRelationship.toCNLen();
        	assertEquals("testRelationshipCNL", "ARelationship is a relationship between FirstEntity and SecondEntity.", newRelationship.getCNLen());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSubsumptionCNL() {
        try {
            String subName = "aSub";
            ObjectType parentEntity = new ObjectType("ParentEntity");
            ObjectType childEntity = new ObjectType("ChildEntity");
        
            Subsumption newSub = new Subsumption(subName, parentEntity, childEntity);
        	
            newSub.toCNLen();
        	assertEquals("testSubsumptionCNL", "Each ChildEntity is a ParentEntity.", newSub.getCNLen());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDisjointObjectTypesCNL() {
        try {
            String subName = "aSub";
            ObjectType parentEntity = new ObjectType("ParentEntity");
            ObjectType childEntity = new ObjectType("ChildEntity");
            ObjectType childEntity2 = new ObjectType("ChildEntity2");
            
            ArrayList<ObjectType> entities = new ArrayList();
            entities.add(childEntity);
            DisjointObjectType newConstraint = new DisjointObjectType(entities);
            
            Subsumption newSub = new Subsumption(subName, parentEntity, childEntity);
            newSub.setDisjointness(newConstraint);
        	
            newSub.toCNLen();
            assertEquals("testDisjointObjectTypesCNL", "Each ChildEntity is a ParentEntity.", newSub.getCNLen());
            assertEquals("testDisjointObjectTypesCNL", "ChildEntity is disjoint with ParentEntity.", newConstraint.getCNLen());
            
            entities.add(childEntity2);
            DisjointObjectType newConstraint2 = new DisjointObjectType(entities);
            
            Subsumption newSub2 = new Subsumption("aSub2", parentEntity, childEntity);
            Subsumption newSub3 = new Subsumption("aSub2", parentEntity, childEntity2);
            newSub2.setDisjointness(newConstraint2);
            newSub3.setDisjointness(newConstraint2);
        	
            newSub2.toCNLen();
            newSub3.toCNLen();
            assertEquals("testDisjointObjectTypesCNL", "Each ChildEntity is a ParentEntity.", newSub2.getCNLen());
            assertEquals("testDisjointObjectTypesCNL", "Each ChildEntity2 is a ParentEntity.", newSub3.getCNLen());
            assertEquals("testDisjointObjectTypesCNL", "ChildEntity and ChildEntity2 is disjoint each other.", newConstraint2.getCNLen());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCompletenessConstraintCNL() {
        try {
            String subName = "aSub";
            ObjectType parentEntity = new ObjectType("ParentEntity");
            ObjectType childEntity = new ObjectType("ChildEntity");
            ObjectType childEntity2 = new ObjectType("ChildEntity2");
            
            ArrayList<ObjectType> entities = new ArrayList();
            entities.add(childEntity);
            CompletenessConstraint newConstraint = new CompletenessConstraint(entities);
            
            Subsumption newSub = new Subsumption(subName, parentEntity, childEntity);
            newSub.setCompleteness(newConstraint);
        	
            newSub.toCNLen();
            assertEquals("testCompletenessConstraintCNL", "Each ChildEntity is a ParentEntity.", newSub.getCNLen());
            assertEquals("testCompletenessConstraintCNL", "ChildEntity covers ParentEntity.", newConstraint.getCNLen());
            
            entities.add(childEntity2);
            CompletenessConstraint newConstraint2 = new CompletenessConstraint(entities);
            
            Subsumption newSub2 = new Subsumption("aSub2", parentEntity, childEntity);
            Subsumption newSub3 = new Subsumption("aSub2", parentEntity, childEntity2);
            newSub2.setCompleteness(newConstraint2);
            newSub3.setCompleteness(newConstraint2);
        	
            newSub2.toCNLen();
            newSub3.toCNLen();
            assertEquals("testCompletenessConstraintCNL", "Each ChildEntity is a ParentEntity.", newSub2.getCNLen());
            assertEquals("testCompletenessConstraintCNL", "Each ChildEntity2 is a ParentEntity.", newSub3.getCNLen());
            assertEquals("testCompletenessConstraintCNL", "ChildEntity and ChildEntity2 covers ParentEntity.", newConstraint2.getCNLen());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testCommonSetCNL() {
        try {
            ObjectType parentEntity = new ObjectType("Person");
            ObjectType childEntity = new ObjectType("Student");
            ObjectType secondEntity = new ObjectType("Institution");
            ObjectType childEntity2 = new ObjectType("Non student");
            
            parentEntity.toCNLen();
            assertEquals("testObjectTypeCNL", "Person is an Object type.", parentEntity.getCNLen());
            childEntity.toCNLen();
            assertEquals("testObjectTypeCNL", "Student is an Object type.", childEntity.getCNLen());
            childEntity2.toCNLen();
            assertEquals("testObjectTypeCNL", "Non student is an Object type.", childEntity2.getCNLen());
            
            secondEntity.toCNLen();
            assertEquals("testObjectTypeCNL", "Institution is an Object type.", secondEntity.getCNLen());
            
            String subName = "aSub";
            Subsumption newSub = new Subsumption(subName, parentEntity, childEntity);
      	  
            newSub.toCNLen();
        	assertEquals("testSubsumptionCNL", "Each Student is a Person.", newSub.getCNLen());
        	
            ArrayList entities = new ArrayList();
            entities.add(childEntity);
            entities.add(secondEntity);
        	
            String relationshipName = "enrolled";
            Relationship newRelationship = new Relationship(relationshipName, entities);
            
            ObjectTypeCardinality card1 = new ObjectTypeCardinality("card1", "1", "*");
            ObjectTypeCardinality card2 = new ObjectTypeCardinality("card2", "2", "*");
            
            Role role_a = new Role("in", childEntity, newRelationship, card1);
            Role role_b = new Role("of", secondEntity, newRelationship, card2);
            
            Mandatory mand_a = new Mandatory("mand1", role_a);
            mand_a.toCNLen();
            Mandatory mand_b = new Mandatory("mand2", role_b);
            mand_b.toCNLen();
            
            ArrayList roles = new ArrayList();
            
        	role_a.toCNLen();
        	assertEquals("testRoleCNL", role_a.getCNLen(), "In is a role in a relationship enrolled, each Student in s at least 1 Institution and each Student in s at most * Institution.");
        	role_b.toCNLen();
        	assertEquals("testRoleCNL", role_b.getCNLen(), "Of is a role in a relationship enrolled, each Institution of s at least 2 Student and each Institution of s at most * Student.");
            
            newRelationship.toCNLen();
        	assertEquals("testRelationshipCNL", "Enrolled is a relationship between Student and Institution.", newRelationship.getCNLen());
        	     	
            ArrayList entitiesDC = new ArrayList();
            entitiesDC.add(childEntity);
            entitiesDC.add(childEntity2);
            
            Subsumption newSub2 = new Subsumption(subName, parentEntity, childEntity2);
            
            DisjointObjectType disjoint = new DisjointObjectType(entitiesDC);
            CompletenessConstraint total = new CompletenessConstraint(entitiesDC);
            
            newSub.setDisjointness(disjoint);
            newSub.setCompleteness(total);
            newSub2.setDisjointness(disjoint);
            newSub2.setCompleteness(total);
        	
            newSub.toCNLen();
            newSub2.toCNLen();
        	
        	System.out.println(parentEntity.getCNLen());
        	System.out.println(childEntity.getCNLen());
        	System.out.println(childEntity2.getCNLen());
        	System.out.println(secondEntity.getCNLen());
        	System.out.println(newSub.getCNLen());
        	System.out.println(newSub2.getCNLen());
        	System.out.println(disjoint.getCNLen());
        	System.out.println(total.getCNLen());
        	System.out.println(newRelationship.getCNLen());
        	System.out.println(role_a.getCNLen());
        	System.out.println(role_b.getCNLen());
        	System.out.println(mand_a.getCNLen());
        	System.out.println(mand_b.getCNLen());       	

        	
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


 

}
