package com.gilia.metamodel.role;

import com.gilia.exceptions.CardinalitySyntaxException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.relationship.Relationship;
import org.json.simple.JSONObject;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Role extends Entity { // TODO: 1:1 Mapping

    private EntityType entity;
    private Relationship relationship;
    private ObjectTypeCardinality cardinalityConstraint;

    /**
     *
     */
    public Role() {
        super();
    }

    /**
     * @param name
     */
    public Role(String name) {
        super(name);
    }

    public Role(String name, EntityType entity, Relationship relationship, String cardinality) throws CardinalitySyntaxException {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraint = new ObjectTypeCardinality(cardinality);
    }

    public Role(String name, EntityType entity, Relationship relationship, String minCardinality, String maxCardinality) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;

    }

    public Role(String name, EntityType entity, Relationship relationship, ObjectTypeCardinality cardinalityObject) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraint = cardinalityObject;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Example:
     * {
     * "rolename": "http://crowd.fi.uncoma.edu.ar/kb1#person",
     * "relationship": "http://crowd.fi.uncoma.edu.ar/kb1#belongs",
     * "entity type": "http://crowd.fi.uncoma.edu.ar/kb1#Person",
     * "object type cardinality": [
     * "http://crowd.fi.uncoma.edu.ar/kb1#card2"
     * ]
     * }
     *
     * @return
     */
    public JSONObject toJSONObject() {
        JSONObject role = new JSONObject();

        role.put("name", name);
        role.put("relationship", relationship.getName());
        role.put("entity type", entity.getName());
        role.put("object type cardinality", cardinalityConstraint.getName());

        return role;
    }
}
