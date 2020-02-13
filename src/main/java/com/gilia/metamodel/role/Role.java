package com.gilia.metamodel.role;

import com.gilia.exceptions.CardinalitySyntaxException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality;
import com.gilia.metamodel.constraint.mandatory.Mandatory;
import com.gilia.metamodel.entitytype.EntityType;
import com.gilia.metamodel.relationship.Relationship;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.*;
import static com.gilia.utils.Utils.getAlphaNumericString;

/**
 * Representation of the Role class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Role extends Entity {

    private EntityType entity;
    private Relationship relationship;
    private ArrayList<ObjectTypeCardinality> cardinalityConstraints;
    private Mandatory mandatoryConstraint;

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
        this(name, entity, relationship, cardinalityObject, null);
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
        this(name, entity, relationship, cardinalities, null);
    }

    /**
     * Creates an instance of a Role with a Mandatory constraint. This constructor receives a ObjectTypeCardinality object that represents the constraint imposed by
     * the cardinality.
     *
     * @param name                String that represents the name of the role
     * @param entity              EntityType object associated to the role to be created
     * @param relationship        Relationship object associated to the role to be created
     * @param cardinalityObject   ObjectTypeCardinality object that represents the cardinality constraint for the role to be created
     * @param mandatoryConstraint Mandatory object that constraints the role
     * @see com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality
     */
    public Role(String name, EntityType entity, Relationship relationship, ObjectTypeCardinality cardinalityObject, Mandatory mandatoryConstraint) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraints = new ArrayList<ObjectTypeCardinality>();
        try {
            if (mandatoryConstraint != null && Integer.parseInt(cardinalityObject.getMinCardinality()) == 0) {
                cardinalityObject.setMinCardinality("1");
            } else if (mandatoryConstraint == null && Integer.parseInt(cardinalityObject.getMinCardinality()) >= 1) {
                mandatoryConstraint = new Mandatory("mandatory" + getAlphaNumericString(4), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cardinalityConstraints.add(cardinalityObject);
        this.mandatoryConstraint = mandatoryConstraint;
    }

    /**
     * Creates an instance of a Role with a Mandatory constraint. This constructor receives a ObjectTypeCardinality object that represents the constraint imposed by
     * the cardinality.
     *
     * @param name                String that represents the name of the role
     * @param entity              EntityType object associated to the role to be created
     * @param relationship        Relationship object associated to the role to be created
     * @param cardinalities       ArrayList of ObjectTypeCardinality object that represents the cardinalities constraints for the role to be created
     * @param mandatoryConstraint Mandatory object that constraints the role
     * @see com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality
     */
    public Role(String name, EntityType entity, Relationship relationship, ArrayList<ObjectTypeCardinality> cardinalities, Mandatory mandatoryConstraint) {
        super(name);
        this.entity = entity;
        this.relationship = relationship;
        this.cardinalityConstraints = cardinalities;
        try {
            if (mandatoryConstraint != null) {
                for (ObjectTypeCardinality cardinalityObject : cardinalities) {
                    if (mandatoryConstraint != null && Integer.parseInt(cardinalityObject.getMinCardinality()) == 0) {
                        cardinalityObject.setMinCardinality("1");
                    } else if (mandatoryConstraint == null && Integer.parseInt(cardinalityObject.getMinCardinality()) >= 1) {
                        mandatoryConstraint = new Mandatory("mandatory" + getAlphaNumericString(4), this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mandatoryConstraint = mandatoryConstraint;
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

    public Mandatory getMandatoryConstraint() {
        return mandatoryConstraint;
    }

    public void setMandatoryConstraint(Mandatory mandatoryConstraint) {
        try {
            if (mandatoryConstraint != null) {
                for (ObjectTypeCardinality cardinalityObject : this.cardinalityConstraints) {
                    if (mandatoryConstraint != null && Integer.parseInt(cardinalityObject.getMinCardinality()) == 0) {
                        cardinalityObject.setMinCardinality("1");
                    } else if (mandatoryConstraint == null && Integer.parseInt(cardinalityObject.getMinCardinality()) >= 1) {
                        mandatoryConstraint = new Mandatory("mandatory" + getAlphaNumericString(4), this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mandatoryConstraint = mandatoryConstraint;
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
                "entity=" + entity.getName() +
                ", relationship=" + relationship.getName() +
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
        if (mandatoryConstraint != null) {
            role.put(KEY_MANDATORY, mandatoryConstraint.getName());
        }
        return role;
    }
}
