package com.gilia.metamodel.relationship

import com.gilia.metamodel.entitytype.objecttype.ObjectType
import com.gilia.metamodel.role.Role
import spock.lang.Specification

class RelationshipTest extends Specification {

    void 'Empty Relationship creation'(){
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

    void 'Relationship creation with name, entities and roles'(){
        when:
        String relationshipName = "MyNewRelationship";
        ObjectType firstEntity = new ObjectType("MyFirstEntity");
        ObjectType secondEntity = new ObjectType("MySecondEntity");
        Role role = new Role();
        Relationship newRelationship = new Relationship(relationshipName);

        then:
        !newRelationship.isNameless()
        newRelationship.getId() != null
        newRelationship.getName() == relationshipName
    }
}
