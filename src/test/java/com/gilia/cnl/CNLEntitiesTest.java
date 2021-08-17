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

import org.json.simple.JSONObject;
import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.Subsumption;
import com.gilia.metamodel.role.Role;


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
    public void testRoleCNL() {
        try {
        	String roleName = "aRole";
            ObjectType entity = new ObjectType("anObjectType");
            Relationship relationship = new Relationship("aRelationship");
            Role role = new Role(roleName, entity, relationship);
        	
        	role.toCNLen();
        	assertEquals("testRoleCNL", role.getCNLen(), "ARole is a role in a relationship aRelationship.");

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
    public void testCommonSetCNL() {
        try {
            ObjectType parentEntity = new ObjectType("Person");
            ObjectType childEntity = new ObjectType("Student");
            ObjectType secondEntity = new ObjectType("Institution");
            
            parentEntity.toCNLen();
            assertEquals("testObjectTypeCNL", "Person is an Object type.", parentEntity.getCNLen());
            childEntity.toCNLen();
            assertEquals("testObjectTypeCNL", "Student is an Object type.", childEntity.getCNLen());
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
            
            Role role_a = new Role("in", childEntity, newRelationship);
            Role role_b = new Role("of", secondEntity, newRelationship);
            
            ArrayList roles = new ArrayList();

            roles.add(role_a);
            roles.add(role_b);
            
            newRelationship.addRoles(roles);
            
        	role_a.toCNLen();
        	assertEquals("testRoleCNL", role_a.getCNLen(), "In is a role in a relationship enrolled.");
        	role_b.toCNLen();
        	assertEquals("testRoleCNL", role_b.getCNLen(), "Of is a role in a relationship enrolled.");
            
            newRelationship.toCNLen();
        	assertEquals("testRelationshipCNL", "Enrolled is a relationship between Student and Institution.", newRelationship.getCNLen());
        	
/*        	System.out.println(parentEntity.getCNLen());
        	System.out.println(childEntity.getCNLen());
        	System.out.println(secondEntity.getCNLen());
        	System.out.println(newSub.getCNLen());
        	System.out.println(role_a.getCNLen());
        	System.out.println(role_b.getCNLen());
        	System.out.println(newRelationship.getCNLen()); */
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


 

}
