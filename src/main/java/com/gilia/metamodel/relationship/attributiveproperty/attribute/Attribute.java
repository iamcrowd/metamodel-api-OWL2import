package com.gilia.metamodel.relationship.attributiveproperty.attribute;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;

import java.util.List;

/**
 * @author Emiliano Rios Gavagnin
 *
 */
public class Attribute extends AttributiveProperty {
    public Attribute(String name) {
        super(name);
    }

    public Attribute(String name, List<Entity> domain, DataType range) {
        super(name, domain, range);
    }
}