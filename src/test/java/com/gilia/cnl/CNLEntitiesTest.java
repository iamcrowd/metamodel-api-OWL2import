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

import org.json.simple.JSONObject;
import com.gilia.metamodel.*;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;


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

 

}
