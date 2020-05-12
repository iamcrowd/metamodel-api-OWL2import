package com.gilia.metamodel.relationship.attributiveproperty.attribute;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.DataType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import com.gilia.metamodel.entitytype.valueproperty.ValueType;
import com.gilia.metamodel.relationship.Relationship;

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
        if (domain.size() > 1 || domain.get(0).getClass() != ValueType.class) {
            // Throw wrong size exception
            // Throw wrong domain type exception
            System.out.println("Wrong size or type");
        }

    }

    @Override
    public void addDomain(Entity newDomain) {
        super.addDomain(newDomain);
        if (this.getDomain().size() > 1 || newDomain.getClass() != ValueType.class) {
            // Throw wrong size exception
            // Throw wrong domain type exception
            System.out.println("Wrong size or type");
        }
    }
}
