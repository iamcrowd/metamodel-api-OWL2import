package com.gilia.metamodel.relationship.attributiveproperty.attribute;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emiliano Rios Gavagnin
 *
 */
public class MappedTo extends Attribute {

    public MappedTo(String name) {
        super(name);
    }

    public MappedTo(String name, List<Entity> domain, DataType range) {
        super(name, domain, range);
        if (domain.size() > 1 || domain.get(0).getClass() != ValueType.class) {
            // Throw wrong size exception
            // Throw wrong domain type exception
            System.out.println("Wrong size or type");
        }
    }

    @Override
    public void setDomain(List<Entity> domain) {
        super.setDomain(domain);
        if (domain.size() > 1 || !domain.get(0).getClass().equals(ValueType.class)) {
            // Throw wrong size exception
            // Throw wrong domain type exception
            System.out.println("Wrong size or type");
        }

    }

    public void addDomain(ObjectType newDomain) {
        super.addDomain(newDomain);
        if (this.getDomain().size() > 1 || !newDomain.getClass().equals(ValueType.class)) {
            // Throw wrong size exception
            // Throw wrong domain type exception
            System.out.println("Wrong size or type");
        }
    }

    public AttributiveProperty toAttributiveProperty() {
        ArrayList<Entity> domain = new ArrayList();
        for (Entity entity : this.getDomain()) {
            domain.add(entity);
        }
        AttributiveProperty attributiveProperty = new AttributiveProperty(this.getName(), domain, this.getRange());
        return attributiveProperty; // Should the generated entities be included in the Metamodel instance? or its generated temporarily?
    }



    @Override
    public JSONObject toJSONObject() {
        return super.toJSONObject();
    }
}
