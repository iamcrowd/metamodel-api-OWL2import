package com.gilia.metamodel.entitytype;

import com.gilia.metamodel.Entity;

/**
 * @author Emiliano Rios Gavagnin
 */
public abstract class EntityType extends Entity {

    public EntityType() {
        super();
    }

    /**
     * @param name
     */
    public EntityType(String name) {
        super(name);
    }
}
