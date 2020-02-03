package com.gilia.metamodel.relationship.attributiveproperty;

import com.gilia.metamodel.relationship.Relationship;
import java.util.ArrayList;

/**
 * @author Emiliano Rios Gavagnin
 */
public class AttributiveProperty extends Relationship {

    public AttributiveProperty(String name) {
        super(name);
    }

    public AttributiveProperty(String name, ArrayList entities, ArrayList roles) {
        super(name, entities, roles);
    }
}
