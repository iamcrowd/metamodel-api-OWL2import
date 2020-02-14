package com.gilia.utils;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.junit.jupiter.api.*;

import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.gilia.utils.ImportUtils.validateOWL;

/**
mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
*/

public class UtilsTest {
    
	@Test
    public void testValidateOWLFromFile() {
		try {
			File file = new File("/var/www/html/metamodelapi-owlimport/src/test/resources/ontologies/gufo.ttl"); 
			validateOWL(file);
		}
		catch (Exception e){
        	e.printStackTrace();
		}
	}
}
