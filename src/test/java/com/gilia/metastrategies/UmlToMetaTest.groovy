package com.gilia.metastrategies

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import spock.lang.Specification

class UmlToMetaTest extends Specification {

    void 'createModel'(){

    }

    void 'identifyClasses'(){
        given:
        JSONParser parser = new JSONParser();
        String umlModelString = "{\"namespaces\":{\"ontologyIRI\":[{\"prefix\":\"crowd\",\"value\":\"http://crowd.fi.uncoma.edu.ar#\"}],\"defaultIRIs\":[{\"prefix\":\"rdf\",\"value\":\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"},{\"prefix\":\"rdfs\",\"value\":\"http://www.w3.org/2000/01/rdf-schema#\"},{\"prefix\":\"xsd\",\"value\":\"http://www.w3.org/2001/XMLSchema#\"},{\"prefix\":\"owl\",\"value\":\"http://www.w3.org/2002/07/owl#\"}],\"IRIs\":[]},\"classes\":[{\"name\":\"http://crowd.fi.uncoma.edu.ar#Class1\",\"attrs\":[],\"methods\":[],\"position\":{\"x\":433,\"y\":179}},{\"name\":\"http://crowd.fi.uncoma.edu.ar#Class2\",\"attrs\":[],\"methods\":[],\"position\":{\"x\":822,\"y\":181}},{\"name\":\"http://crowd.fi.uncoma.edu.ar#Class3\",\"attrs\":[],\"methods\":[],\"position\":{\"x\":724,\"y\":353}},{\"name\":\"http://crowd.fi.uncoma.edu.ar#Class4\",\"attrs\":[],\"methods\":[],\"position\":{\"x\":935,\"y\":350}}],\"links\":[{\"name\":\"http://crowd.fi.uncoma.edu.ar#Rel1\",\"classes\":[\"http://crowd.fi.uncoma.edu.ar#Class1\",\"http://crowd.fi.uncoma.edu.ar#Class2\"],\"multiplicity\":[\"0..*\",\"0..*\"],\"roles\":[\"http://crowd.fi.uncoma.edu.ar#role1\",\"http://crowd.fi.uncoma.edu.ar#role2\"],\"type\":\"association\"},{\"name\":\"http://crowd.fi.uncoma.edu.ar#s1\",\"parent\":\"http://crowd.fi.uncoma.edu.ar#Class2\",\"classes\":[\"http://crowd.fi.uncoma.edu.ar#Class4\",\"http://crowd.fi.uncoma.edu.ar#Class3\"],\"multiplicity\":null,\"roles\":null,\"type\":\"generalization\",\"constraint\":[\"disjoint\",\"covering\"],\"position\":{\"x\":866,\"y\":310}}],\"owllink\":[\"\"]}";
        JSONObject umlModelObject = parser.parse(umlModelString);
        JSONArray umlClasses = umlModelObject.get("classes");
        UmlToMeta model = new UmlToMeta();

        String firstExpectedEntityType = "http://crowd.fi.uncoma.edu.ar#Class1"
        String secondExpectedEntityType = "http://crowd.fi.uncoma.edu.ar#Class2"
        String thirdExpectedEntityType = "http://crowd.fi.uncoma.edu.ar#Class3"
        String fourthExpectedEntityType = "http://crowd.fi.uncoma.edu.ar#Class4"

        when:
        model.identifyClasses(umlClasses);
        JSONObject metaModel = model.getModel();

        then:
        JSONObject entitiesType = metaModel.get("Entity type")
        JSONArray objectsType = entitiesType.get("Object type");
        objectsType.get(0).equals(firstExpectedEntityType)
        objectsType.get(1).equals(secondExpectedEntityType)
        objectsType.get(2).equals(thirdExpectedEntityType)
        objectsType.get(3).equals(fourthExpectedEntityType)


    }
}
