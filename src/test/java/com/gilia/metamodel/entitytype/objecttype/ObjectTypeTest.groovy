package com.gilia.metamodel.entitytype.objecttype

import com.gilia.metamodel.entitytype.EntityType
import org.json.simple.JSONObject
import spock.lang.Specification

import static com.gilia.utils.Constants.KEY_ATTRS
import static com.gilia.utils.Constants.KEY_METHODS
import static com.gilia.utils.Constants.KEY_NAME

class ObjectTypeTest extends Specification {

    final String objectTypeName = "MyNewObjectType";

    void 'Empty Object Type creation'() {
        when:
        ObjectType objectType = new ObjectType();

        then:
        objectType.getId() != null;
        objectType.isNameless();
        objectType.getClass() == ObjectType.class;
    }

    void 'Object Type creation with name'() {
        when:
        ObjectType objectType = new ObjectType(objectTypeName);

        then:
        objectType.getId() != null;
        !objectType.isNameless();
        objectType.getName() == objectTypeName;
        objectType.getClass() == ObjectType.class;
    }

    void 'Object Type JSONObject creation'() {
        given:
        ObjectType objectType = new ObjectType(objectTypeName);

        when:
        JSONObject jsonObject = objectType.toJSONObject();

        then:
        jsonObject.containsKey(KEY_NAME)
        jsonObject.get(KEY_NAME) == objectTypeName;
    }

    void 'Object Type to UML class'() {
        given:
        ObjectType objectType = new ObjectType(objectTypeName);

        when:
        JSONObject umlJsonObject = objectType.toUML();

        then:
        umlJsonObject.containsKey(KEY_NAME)
        umlJsonObject.containsKey(KEY_ATTRS)
        umlJsonObject.containsKey(KEY_METHODS)
        umlJsonObject.get(KEY_NAME) == objectTypeName;
    }

    void 'Object Type to String'() {
        given:
        ObjectType objectType = new ObjectType(objectTypeName);

        when:
        String objectTypeString = objectType.toString();

        then:
        objectTypeString == "ObjectType{id='" + objectType.getId() + "', name='" + objectType.getName() + "'}";
    }
}
