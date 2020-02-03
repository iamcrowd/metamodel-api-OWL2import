package com.gilia.metamodel;

import java.util.Objects;
import java.util.UUID;

/**
 * Representation of the Entity class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public abstract class Entity {

    protected final String id;
    protected String name;

    public Entity() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
    }

    public Entity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        if (Objects.equals(id, entity.id)) return true;
        return Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public boolean isNameless() { // Used mainly for checking existence. It is a way to avoid checking/returning nulls
        return name.equals("") || name == null;
    }
}
