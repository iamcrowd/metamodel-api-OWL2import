package com.gilia.metamodel.entitytype.valueproperty;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.relationship.attributiveproperty.attribute.MappedTo;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * @author Emiliano Rios Gavagnin
 */
public class ValueType extends ValueProperty {

    MappedTo mappedTo;

    /**
     * @param name
     */
    public ValueType(String name) {
        super(name);
    }

    public ValueType(String name, List<ObjectType> domain) {
        super(name, domain);
    }

    public ValueType(String name, ObjectType domain) {
        super(name, domain);
    }

    public void setMappedTo(MappedTo mappedTo) {
        this.mappedTo = mappedTo;
    }

    /**
     * Generates a JSONObject that represents the information of the Object type according to the
     * ORM language. The JSONObject generated respects the ORM Schema.
     *
     * @return JSONObject that represents the equivalent ORM Entity.
     */
    public JSONObject toORM() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, KEY_VALUE);
        return jsonObject;
    }

    public AttributiveProperty toAttributiveProperty() {
        ArrayList<Entity> domain = new ArrayList();
        for (Entity entity : this.getDomain()) {
            domain.add(entity);
        }

        AttributiveProperty attributiveProperty = new AttributiveProperty(this.getName(), domain, mappedTo.getRange());
        return attributiveProperty; // Should the generated entities be included in the Metamodel instance? or its generated temporarily?
    }
}
