package com.gilia.metamodel.relationship

import com.gilia.exceptions.MetamodelDefinitionCompromisedException
import com.gilia.metamodel.entitytype.objecttype.ObjectType
import com.gilia.metamodel.role.Role
import com.gilia.metamodel.relationship.Relationship
import org.json.simple.JSONObject
import spock.lang.Specification

import static com.gilia.utils.Constants.KEY_ASSOCIATION
import static com.gilia.utils.Constants.KEY_CLASSES
import static com.gilia.utils.Constants.KEY_ENTITIES
import static com.gilia.utils.Constants.KEY_MULTIPLICITY
import static com.gilia.utils.Constants.KEY_NAME
import static com.gilia.utils.Constants.KEY_ROLES
import static com.gilia.utils.Constants.KEY_TYPE
import static com.gilia.utils.Constants.RELATIONSHIP_DEFINITION_ERROR

class RelationshipTest extends Specification {

    void 'Empty Relationship creation'() {
        when:
        Relationship newRelationship = new Relationship();

        then:
        newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getEntities().size() == 0
        newRelationship.getRoles().size() == 0
    }

    void 'Relationship creation with name'() {
        when:
        String relationshipName = "MyNewRelationship";
        Relationship newRelationship = new Relationship(relationshipName);

        then:
        !newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getName() == relationshipName
    }

    void 'Relationship creation with name and roles'() {
        when:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");

        ArrayList entities = new ArrayList();
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities);

        then:
        !newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getName() == relationshipName
        newRelationship.getEntities().contains(firstEntity)
        newRelationship.getEntities().contains(secondEntity)
    }

    void 'Relationship creation with name, entities and roles'() {
        when:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role role = new Role("MyRole");

        ArrayList roles = new ArrayList()
        ArrayList entities = new ArrayList();
        roles.add(role);
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities, roles);

        then:
        !newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getName() == relationshipName
        newRelationship.getEntities().contains(firstEntity)
        newRelationship.getEntities().contains(secondEntity)
        newRelationship.getRoles().contains(role)
    }

    void 'Add new Role to Relationship'() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role role = new Role("MyRole");

        ArrayList roles = new ArrayList()
        ArrayList entities = new ArrayList();
        roles.add(role);
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities, roles);

        when:
        Role anotherRole = new Role("AnotherRole");
        newRelationship.addRole(anotherRole);

        then:
        !newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getName() == relationshipName
        newRelationship.getEntities().contains(firstEntity)
        newRelationship.getEntities().contains(secondEntity)
        newRelationship.getRoles().contains(role)
        newRelationship.getRoles().contains(anotherRole)
    }

    void 'Add new Role to Relationship with already two Roles'() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role role = new Role("MyRole");
        Role anotherRole = new Role("AnotherRole");

        ArrayList roles = new ArrayList()
        ArrayList entities = new ArrayList();
        roles.add(role);
        roles.add(anotherRole);
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities, roles);

        when:
        Role thirdRole = new Role("ThirdRole");
        newRelationship.addRole(thirdRole);

        then:
        def exception = thrown(MetamodelDefinitionCompromisedException);
        exception.message == "MetamodelException - MetamodelDefinitionCompromisedException - " + RELATIONSHIP_DEFINITION_ERROR;
    }

    void 'Add new Role to Relationship with repeated Role'() { // TODO: Should this be an exception?
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role role = new Role("MyRole");

        ArrayList roles = new ArrayList()
        ArrayList entities = new ArrayList();
        roles.add(role);
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities, roles);

        when:
        newRelationship.addRole(role);

        then:
        def exception = thrown(MetamodelDefinitionCompromisedException);
        exception.message == "MetamodelException - MetamodelDefinitionCompromisedException - " + RELATIONSHIP_DEFINITION_ERROR;
    }

    void 'Relationship to UML relationship'() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");

        ArrayList entities = new ArrayList();
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities);

        Role firstRole = new Role("MyRole", firstEntity, newRelationship, "0..3");
        Role secondRole = new Role("AnotherRole", secondEntity, newRelationship, "1..5");
        ArrayList roles = new ArrayList()
        roles.add(firstRole);
        roles.add(secondRole);
        newRelationship.setRoles(roles);

        when:
        JSONObject umlJsonObject = newRelationship.toUML();

        then:
        umlJsonObject.containsKey(KEY_NAME)
        umlJsonObject.containsKey(KEY_TYPE)
        umlJsonObject.containsKey(KEY_ROLES)
        umlJsonObject.containsKey(KEY_MULTIPLICITY)
        umlJsonObject.containsKey(KEY_CLASSES)
        umlJsonObject.get(KEY_NAME) == relationshipName
        umlJsonObject.get(KEY_TYPE) == KEY_ASSOCIATION
        umlJsonObject.get(KEY_MULTIPLICITY).contains("1..5")
        umlJsonObject.get(KEY_MULTIPLICITY).contains("0..3")
        umlJsonObject.get(KEY_ROLES).contains("MyRole")
        umlJsonObject.get(KEY_ROLES).contains("AnotherRole")
        umlJsonObject.get(KEY_CLASSES).contains("MyFirstEntity")
        umlJsonObject.get(KEY_CLASSES).contains("MySecondEntity")
    }

    void 'Relationship to UML relationship with less than two roles'() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role newRole = new Role("MyRole");

        ArrayList entities = new ArrayList();
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities);
        newRelationship.addRole(newRole);

        when:
        JSONObject umlJsonObject = newRelationship.toUML();

        then:
        def exception = thrown(MetamodelDefinitionCompromisedException);
        exception.message == "MetamodelException - MetamodelDefinitionCompromisedException - " + "Can not generate UML for Relationship " + relationshipName + ". Relationship definition has been violated.";
    }

    void 'Relationship to UML relationship with roles '() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role firstRole = new Role("MyRole");
        Role secondRole = new Role("AnotherRole");

        ArrayList entities = new ArrayList();
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities);
        newRelationship.addRole(firstRole);
        newRelationship.addRole(secondRole);

        when:
        JSONObject umlJsonObject = newRelationship.toUML();

        then:
        def exception = thrown(MetamodelDefinitionCompromisedException);
        exception.message == "MetamodelException - MetamodelDefinitionCompromisedException - " + "Can not generate UML for Relationship " + relationshipName + ". Role definition has been violated.";
    }

    // TODO: Implement toString test for Relationship
    /*
    void 'Relationship to string'() {
        given:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");

        ArrayList entities = new ArrayList();
        entities.add(firstEntity);
        entities.add(secondEntity);

        Relationship newRelationship = new Relationship(relationshipName, entities);

        Role firstRole = new Role("MyRole", firstEntity, newRelationship, "0..3");
        Role secondRole = new Role("AnotherRole", secondEntity, newRelationship, "1..5");
        ArrayList roles = new ArrayList()
        roles.add(firstRole);
        roles.add(secondRole);
        newRelationship.setRoles(roles);

        when:
        String relationshipString = newRelationship.toString();

        then:
        relationshipString == "Relationship{entities=[ObjectType{id='" + firstEntity.getId() + "', name='MyFirstEntity'}, ObjectType{id='" + secondEntity.getId() + "', name='MySecondEntity'}], roles=[Role{entity=MyFirstEntity, relationship=MyNewRelationship, cardinalityConstraint=[ObjectTypeCardinality{minCardinality='0', maxCardinality='3', id='287bab87-a485-4a61-9257-90d985c1f017', name=''}], id='85861ba0-ebf7-4eef-a2e8-e8474596ed51', name='MyRole'}, Role{entity=MySecondEntity, relationship=MyNewRelationship, cardinalityConstraint=[ObjectTypeCardinality{minCardinality='1', maxCardinality='5', id='2434eb87-426e-442f-8f06-2bb528c77a74', name=' '}], id='9f497b57-8a01-4b8d-b5fa-dd623942a497', name='AnotherRole'}], id='" + newRelationship.getId() + "', name='MyNewRelationship'}"
    }*/


}
