package com.gilia.metamodel.entitytype.valueproperty;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emiliano Rios Gavagnin
 */
public abstract class ValueProperty extends EntityType {

    private List<ObjectType> domain;

    /**
     * @param name
     */
    public ValueProperty(String name) {
        super(name);
    }

    public ValueProperty(String name, List<ObjectType> domain) {
        super(name);
        this.domain = domain;
    }

    public ValueProperty(String name, ObjectType domain){
       super(name);
       this.domain = new ArrayList();
       this.domain.add(domain);
    }

    public List<ObjectType> getDomain(){
        return domain;
    }

    public void addDomain(ObjectType domain){
        if(this.domain == null){
            this.domain = new ArrayList<>();
        }
        this.domain.add(domain);
    }


}
