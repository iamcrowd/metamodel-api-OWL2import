package com.gilia.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

//import static groovy.test.GroovyAssert.*

//import spock.lang.Specification;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.gilia.utils.Utils.validateOWL;

/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class UtilsTest {

	@Test
    public void testLoadOWL2FileOK() {
    	try {
 //   		String owlfile = new String(UtilsTest.class.getClassLoader().getResource("ontologies/pizza.owl").toString()); 
    	  	validateOWL("/home/gab/Documentos/KF/metamodelapi-owlimport/target/test-classes/ontologies/pizza.owl");
    	} 
    	catch (Exception e){
        	e.printStackTrace();
    	}


    }
    
}
