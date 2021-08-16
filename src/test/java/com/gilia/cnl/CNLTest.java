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

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;



/**
 * mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
 */
public class CNLTest {

    @Test
    public void testLib() {
        try {
        	
        	  Lexicon lexicon = Lexicon.getDefaultLexicon();
        	  NLGFactory nlgFactory = new NLGFactory(lexicon);
        	  Realiser realiser = new Realiser(lexicon);
        	  NLGElement s1 = nlgFactory.createSentence("Hello World! This is a first test for CNLcore");
        	  String output = realiser.realiseSentence(s1);
        	  assertEquals("testLib", output, "Hello World! This is a first test for CNLcore");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testToyExample() {
        try {
        	
        	  Lexicon lexicon = Lexicon.getDefaultLexicon();
        	  NLGFactory nlgFactory = new NLGFactory(lexicon);
        	  Realiser realiser = new Realiser(lexicon);
        	  
        	  SPhraseSpec p = nlgFactory.createClause();
        	  p.setSubject("Person");
        	  p.setVerb("is");
        	  p.setObject("an Object type");
        	  
        	  String output = realiser.realiseSentence(p);
        	  assertEquals("testToyExample", output, "Person is an Object type.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 

}
