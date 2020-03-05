package com.gilia.metamodel.role

import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality
import com.gilia.metamodel.entitytype.objecttype.ObjectType
import com.gilia.metamodel.relationship.Relationship
import com.gilia.metamodel.role.Role
import org.json.simple.JSONObject
import spock.lang.Specification

import static com.gilia.utils.Constants.KEY_ENTITY_TYPE
import static com.gilia.utils.Constants.KEY_OBJECT_TYPE_CARDINALITY
import static com.gilia.utils.Constants.KEY_ROLENAME
import static com.gilia.utils.Constants.RELATIONSHIP_STRING

class RoleTest extends Specification {

    void 'Empty Role creation'() {
        when:
        Role newRole = new Role();

        then:
        newRole.isNameless()
        newRole.getId() != null
    }

    void 'Role creation with name'() {
        when:
        String roleName = "MyNewRole";
        Role newRole = new Role(roleName);

        then:
        !newRole.isNameless()
        newRole.getId() != null
        newRole.getName() == roleName
    }

    void 'Role creation with name, entity, relationship and cardinality object'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality cardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        Role newRole = new Role(roleName, entity, relationship, cardinality);

        then:
        newRole.getId() != null
        newRole.getName() == roleName
        newRole.getEntity() == entity
        newRole.getRelationship() == relationship
        newRole.getCardinalityConstraints().size() == 1
        newRole.getCardinalityConstraints().get(0) == cardinality
    }

    void 'Role creation with name, entity, relationship and cardinality objects'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role newRole = new Role(roleName, entity, relationship, cardinalities);

        then:
        newRole.getId() != null
        newRole.getName() == roleName
        newRole.getEntity() == entity
        newRole.getRelationship() == relationship
        newRole.getCardinalityConstraints().size() == 2
        newRole.getCardinalityConstraints().get(0) == firstCardinality
        newRole.getCardinalityConstraints().get(1) == secondCardinality
    }

    void 'Role equal comparison'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role firstRole = new Role(roleName, entity, relationship, cardinalities);

        roleName = "MyNewRole";
        entity = new ObjectType("MyObjectType");
        relationship = new Relationship("MyRelationship");
        firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role secondRole = new Role(roleName, entity, relationship, cardinalities);

        then:
        firstRole.equals(secondRole)
    }

    void 'Different roles by name'() {
        when:
        String roleName = "MyFirstRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role firstRole = new Role(roleName, entity, relationship, cardinalities);

        roleName = "MySecondRole";
        entity = new ObjectType("MyObjectType");
        relationship = new Relationship("MyRelationship");
        firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role secondRole = new Role(roleName, entity, relationship, cardinalities);

        then:
        !firstRole.equals(secondRole)
    }

    void 'Different roles by entities'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("OneObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role firstRole = new Role(roleName, entity, relationship, cardinalities);

        roleName = "MyNewRole";
        entity = new ObjectType("AnotherObjectType");
        relationship = new Relationship("MyRelationship");
        firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role secondRole = new Role(roleName, entity, relationship, cardinalities);

        then:
        !firstRole.equals(secondRole)
    }

    void 'Different roles by relationships'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("OneRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role firstRole = new Role(roleName, entity, relationship, cardinalities);

        roleName = "MyNewRole";
        entity = new ObjectType("MyObjectType");
        relationship = new Relationship("AnotherRelationship");
        firstCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        secondCardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role secondRole = new Role(roleName, entity, relationship, cardinalities);

        then:
        !firstRole.equals(secondRole)
    }

    void 'Different roles by cardinalities names'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality cardinality = new ObjectTypeCardinality("OneConstraint", "1..12");
        Role firstRole = new Role(roleName, entity, relationship, cardinality);

        roleName = "MyNewRole";
        entity = new ObjectType("MyObjectType");
        relationship = new Relationship("MyRelationship");
        cardinality = new ObjectTypeCardinality("AnotherConstraint", "1..12");
        Role secondRole = new Role(roleName, entity, relationship, cardinality);

        then:
        !firstRole.equals(secondRole)
    }

    void 'Different roles by cardinalities'() {
        when:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality cardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        Role firstRole = new Role(roleName, entity, relationship, cardinality);

        roleName = "MyNewRole";
        entity = new ObjectType("MyObjectType");
        relationship = new Relationship("MyRelationship");
        cardinality = new ObjectTypeCardinality("MyConstraint", "1..*");
        Role secondRole = new Role(roleName, entity, relationship, cardinality);

        then:
        !firstRole.equals(secondRole)
    }

    void 'Role toJSONObject'() {
        given:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality cardinality = new ObjectTypeCardinality("MyConstraint", "1..12");
        Role newRole = new Role(roleName, entity, relationship, cardinality);

        when:
        JSONObject json = newRole.toJSONObject()

        then:
        json.get(KEY_ROLENAME) == roleName
        json.get(RELATIONSHIP_STRING) == "MyRelationship"
        json.get(KEY_ENTITY_TYPE) == "MyObjectType"
        json.get(KEY_OBJECT_TYPE_CARDINALITY).get(0) == "MyConstraint"
    }

    void 'Role toJSONObject with multiples cardinalities'() {
        given:
        String roleName = "MyNewRole";
        ObjectType entity = new ObjectType("MyObjectType");
        Relationship relationship = new Relationship("MyRelationship");
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("FirstConstraint", "1..12");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("SecondConstraint", "1..12");
        ArrayList cardinalities = new ArrayList();
        cardinalities.add(firstCardinality);
        cardinalities.add(secondCardinality);
        Role newRole = new Role(roleName, entity, relationship, cardinalities);

        when:
        JSONObject json = newRole.toJSONObject()

        then:
        json.get(KEY_ROLENAME) == roleName
        json.get(RELATIONSHIP_STRING) == "MyRelationship"
        json.get(KEY_ENTITY_TYPE) == "MyObjectType"
        json.get(KEY_OBJECT_TYPE_CARDINALITY).get(0) == "FirstConstraint"
        json.get(KEY_OBJECT_TYPE_CARDINALITY).get(1) == "SecondConstraint"
    }


}
