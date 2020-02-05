package com.gilia.metamodel.relationship;

import com.gilia.exceptions.EntityNotValidException;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.objecttype.ObjectType;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the Subsumption class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Subsumption extends Relationship {

    protected ObjectType parent;
    protected ObjectType child;
    protected CompletenessConstraint completeness;
    protected DisjointObjectType disjointness;

    /**
     * Creates a basic instance of a Subsumption. It will be created only with the name of the subsumption. Any other information will be missing.
     *
     * @param name String that represents the name of the subsumption
     */
    public Subsumption(String name) {
        super(name);
        this.parent = null;
    }

    /**
     * Creates an instance of a Subsumption. This constructor receives information about the name of the subsumption,
     * the parent entity, and the entity involved in the subsumption (child)
     *
     * @param name   String that represents the name of the subsumption
     * @param parent ObjectType that represents the parent entity
     * @param child  ObjectType that represents the child entity
     */
    public Subsumption(String name, ObjectType parent, ObjectType child) {
        super(name, null, null);
        this.parent = parent;
        this.child = child;
    }

    public Subsumption(String name, ObjectType parent, ObjectType child, CompletenessConstraint completeness, DisjointObjectType disjointness) {
        super(name, null, null);
        this.parent = parent;
        this.child = child;
        this.completeness = completeness;
        this.disjointness = disjointness;
    }

    public Subsumption(String name, ObjectType parent, ObjectType child, CompletenessConstraint completeness) {
        super(name, null, null);
        this.parent = parent;
        this.child = child;
        this.completeness = completeness;
        this.disjointness = null;
    }

    public Subsumption(String name, ObjectType parent, ObjectType child, DisjointObjectType disjointness) {
        super(name, null, null);
        this.parent = parent;
        this.child = child;
        this.completeness = null;
        this.disjointness = disjointness;
    }

    public Subsumption(String name, ObjectType parent, ObjectType child, ArrayList<Constraint> constraints) {
        super(name, null, null);
        this.parent = parent;
        this.child = child;

        for (int i = 0; i < constraints.size(); i++) {
            if (constraints.get(i).getClass() == DisjointObjectType.class) {
                this.disjointness = (DisjointObjectType) constraints.get(i);
            } else if (constraints.get(i).getClass() == CompletenessConstraint.class) {
                this.completeness = (CompletenessConstraint) constraints.get(i);
            } else {
                throw new EntityNotValidException(ENTITY_NOT_FOUND_ERROR);
            }
        }

    }

    public ObjectType getParent() {
        return parent;
    }

    public void setParent(ObjectType parent) {
        this.parent = parent;
    }

    public ObjectType getChild() {
        return child;
    }

    public void setChild(ObjectType child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subsumption that = (Subsumption) o;
        return Objects.equals(parent, that.parent) &&
                Objects.equals(child, that.child) &&
                Objects.equals(completeness, that.completeness) &&
                Objects.equals(disjointness, that.disjointness);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parent);
    }

    @Override
    public String toString() {
        return "Subsumption{" +
                "parent=" + parent +
                ", child=" + child +
                ", disjointness constraint=" + disjointness +
                ", completeness constraint=" + completeness +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Creates a JSONObject with the relevant information about the Subsumption object. The JSON format is based on the
     * Metamodel JSON Schema.
     * <p>
     * Example: <p>
     * { <p>
     * "entity children": "http://crowd.fi.uncoma.edu.ar#Class4", <p>
     * "name": "http://crowd.fi.uncoma.edu.ar#s1", <p>
     * "entity parent": "http://crowd.fi.uncoma.edu.ar#Class2" <p>
     * }
     *
     * @return JSONObject with information about the Subsumption object
     */
    @Override
    public JSONObject toJSONObject() {
        JSONObject subsumption = new JSONObject();

        subsumption.put(KEY_NAME, name);
        subsumption.put(KEY_ENTITY_PARENT, parent.getName());
        subsumption.put(KEY_ENTITY_CHILD, child.getName());
        if (disjointness != null && !disjointness.isNameless()) {
            subsumption.put(KEY_DISJOINTNESS_CONSTRAINT, disjointness.getName());
        }
        if (completeness != null && !completeness.isNameless()) {
            subsumption.put(KEY_COMPLETENESS_CONSTRAINT, completeness.getName());
        }

        return subsumption;
    }
}
