package com.gilia.metamodel.entitytype.valueproperty;

import com.gilia.metamodel.entitytype.EntityType;

/**
 *
 * @author Emiliano Rios Gavagnin
 */
public abstract class ValueProperty extends EntityType {
    protected String name;
    protected String domain; // TODO: Debería ser un String (asociado a un solo entity) o un Array (asociado a varios entity)?
    protected String valueType;

    /**
     * @param name
     */
    public ValueProperty(String name) {
        super(name);
    }
}
