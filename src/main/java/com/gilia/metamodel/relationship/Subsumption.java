package com.gilia.metamodel.relationship;

import com.gilia.metamodel.entitytype.objecttype.ObjectType;

import java.util.ArrayList;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Subsumption extends Relationship {  // TODO: 1:1 Mapping

    protected ObjectType parent;

    public Subsumption(String name) {
        super(name);
        this.parent = null;
    }

    public Subsumption(String name, ObjectType parent, ArrayList entities) {
        super(name, entities, null);
        this.parent = parent;
    }

}
