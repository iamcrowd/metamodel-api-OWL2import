package com.gilia.metamodel.role;

import com.gilia.exceptions.CardinalitySyntaxException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.relationship.Relationship;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the Role class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Role extends Entity {

    private EntityType entity;
    private Relationship relationship;
    private ArrayList<ObjectTypeCardinality> cardinalityConstraints;

    /**
     * Creates a basic instance of a Role. It will be created without information. The only information generated will be an id.
     */
    public Role() {
        super();
    }

    /**
     * Creates a basic instance of a Role. It will be created only with the name of the role. Any other information will be missing.
     *
     * @param name String that represents the name of the role
     */
    public Role(String name) {
        super(name);
    }

    /**
     * Creates an instance of a Role. This constructor receives a cardinality string that respects the CARDINALITY_REGEX
     *
     * @param name         String that represents the name of the role
     * @param entity       EntityType object associated to the role to be created
     * @param relationship Relationship object associated to the role to be created
     * @param cardinality  String that represents the cardinality of the given role, entity and relationship. It must respect the CARDINALITY_REGEX
     * @throws CardinalitySyntaxException
     * @see com.gilia.utils.Constants#CARDINALITY_REGEX
     */
    @Deprecated
    public Role(String name, EntityType entity, Relationship relationship, String cardinality) throws CardinalitySyntaxException {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraints = new ArrayList<ObjectTypeCardinality>();
        cardinalityConstraints.add(new ObjectTypeCardinality(cardinality));
    }

    /**
     * Creates an instance of a Role. This constructor receives a ObjectTypeCardinality object that represents the constraint imposed by
     * the cardinality.
     *
     * @param name              String that represents the name of the role
     * @param entity            EntityType object associated to the role to be created
     * @param relationship      Relationship object associated to the role to be created
     * @param cardinalityObject ObjectTypeCardinality object that represents the cardinality constraint for the role to be created
     * @see com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality
     */
    public Role(String name, EntityType entity, Relationship relationship, ObjectTypeCardinality cardinalityObject) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraints = new ArrayList<ObjectTypeCardinality>();
        cardinalityConstraints.add(cardinalityObject);
    }

    /**
     * Creates an instance of a Role. This constructor receives a ObjectTypeCardinality object that represents the constraint imposed by
     * the cardinality.
     *
     * @param name          String that represents the name of the role
     * @param entity        EntityType object associated to the role to be created
     * @param relationship  Relationship object associated to the role to be created
     * @param cardinalities ArrayList of ObjectTypeCardinality object that represents the cardinalities constraints for the role to be created
     * @see com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality
     */
    public Role(String name, EntityType entity, Relationship relationship, ArrayList<ObjectTypeCardinality> cardinalities) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraints = cardinalities;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public ArrayList<ObjectTypeCardinality> getCardinalityConstraints() {
        return cardinalityConstraints;
    }

    public void setCardinalityConstraints(ArrayList<ObjectTypeCardinality> cardinalityConstraint) {
        this.cardinalityConstraints = cardinalityConstraint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Role role = (Role) o;
        return Objects.equals(entity, role.entity) &&
                Objects.equals(relationship, role.relationship) &&
                Objects.equals(cardinalityConstraints, role.cardinalityConstraints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entity, relationship, cardinalityConstraints);
    }

    @Override
    public String toString() {
        return "Role{" +
                "entity=" + entity +
                ", relationship=" + relationship +
                ", cardinalityConstraint=" + cardinalityConstraints +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Creates a JSONObject with the relevant information about the Role object. The JSON format is based on the
     * Metamodel JSON Schema.
     * <p>
     * Example: <p>
     * { <p>
     * "rolename": "http://crowd.fi.uncoma.edu.ar/kb1#person", <p>
     * "relationship": "http://crowd.fi.uncoma.edu.ar/kb1#belongs", <p>
     * "entity type": "http://crowd.fi.uncoma.edu.ar/kb1#Person", <p>
     * "object type cardinality": [ <p>
     * "http://crowd.fi.uncoma.edu.ar/kb1#card2" <p>
     * ] <p>
     * } <p>
     *
     * @return JSONObject with information about the Role object
     */
    public JSONObject toJSONObject() {
        JSONObject role = new JSONObject();

        JSONArray cardinalities = new JSONArray();
        for (ObjectTypeCardinality cardinality : cardinalityConstraints) {
            cardinalities.add(cardinality.getName());
        }


        role.put(KEY_ROLENAME, name);
        role.put(RELATIONSHIP_STRING, relationship.getName());
        role.put(KEY_ENTITY_TYPE, entity.getName());
        role.put(KEY_OBJECT_TYPE_CARDINALITY, cardinalities);

        return role;
    }
}
