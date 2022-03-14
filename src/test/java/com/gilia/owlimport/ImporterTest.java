package com.gilia.owlimport;

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

import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import org.semanticweb.owlapi.apibinding.*;

import static com.gilia.utils.ImportUtils.validateOWL;
import com.gilia.metamodel.*;
import com.gilia.builder.metabuilder.*;

/**
 * mvn clean test -Dtest=UtilsTest -DfailIfNoTests=false
 */
@DisplayName("Test Suite")
public class ImporterTest {

    @Test
    public void testAtomAtom() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1a.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println(importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomDisjunctionOfAtoms() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/1b.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println(importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomComplementOf() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/complement.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println("This JSON %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomDisjointAtom() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/disjoint.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println(importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomExistentialOfAtom() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/2a.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println(importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtomUniversalOfAtom() {
        try {
            String path = new String(ImporterTest.class.getClassLoader().getResource("metamodels/3withExistsProp.owl").toString());
            String[] owlfilepath = path.split(":", 2);
            OWLImporter importer = new OWLImporter(true);
            importer.loadFromPath(owlfilepath[1]);
            importer.translate();
            System.out.println(importer.toJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
