package com.gilia.metamodel.relationship;

import com.gilia.exceptions.EntityNotValidException;
import com.gilia.exceptions.MetamodelDefinitionCompromisedException;
import com.gilia.metamodel.Entity;
import com.gilia.metamodel.constraint.CompletenessConstraint;
import com.gilia.metamodel.constraint.Constraint;
import com.gilia.metamodel.constraint.disjointness.DisjointObjectType;
import com.gilia.metamodel.entitytype.Qualifier;
import com.gilia.metamodel.relationship.attributiveproperty.AttributiveProperty;
import com.gilia.metamodel.role.Role;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the Subsumption class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Subsumption extends Relationship {

    protected Entity parent;
    protected Entity child;
    protected CompletenessConstraint completeness;
    protected DisjointObjectType disjointness;

    /**
     * Creates a basic instance of a Subsumption. It will be created only with the name of the subsumption. Any other information will be missing.
     *
     * @param name String that represents the name of the subsumption
     */
    public Subsumption(String name) {
        super(name);
    }

    /**
     * Creates an instance of a Subsumption. This constructor receives information about the name of the subsumption,
     * the parent entity, and the entity involved in the subsumption (child)
     *
     * @param name   String that represents the name of the subsumption
     * @param parent Entity that represents the parent entity
     * @param child  Entity that represents the child entity
     */
    public Subsumption(String name, Entity parent, Entity child) {
        super(name, null, null);
        if (!isValidEntityForSubsumption(parent) || !isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.parent = parent;
        this.child = child;
    }

    public Subsumption(String name, Entity parent, Entity child, CompletenessConstraint completeness, DisjointObjectType disjointness) {
        super(name, null, null);
        if (!isValidEntityForSubsumption(parent) || !isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.parent = parent;
        this.child = child;
        this.completeness = completeness;
        this.disjointness = disjointness;
    }

    public Subsumption(String name, Entity parent, Entity child, CompletenessConstraint completeness) {
        super(name, null, null);
        if (!isValidEntityForSubsumption(parent) || !isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.parent = parent;
        this.child = child;
        this.completeness = completeness;
        this.disjointness = null;
    }

    public Subsumption(String name, Entity parent, Entity child, DisjointObjectType disjointness) {
        super(name, null, null);
        if (!isValidEntityForSubsumption(parent) || !isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.parent = parent;
        this.child = child;
        this.completeness = null;
        this.disjointness = disjointness;
    }

    public Subsumption(String name, Entity parent, Entity child, List<Constraint> constraints) {
        super(name, null, null);
        if (!isValidEntityForSubsumption(parent) || !isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
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

    public Entity getParent() {
        return parent;
    }

    public void setParent(Entity parent) {
        if (!isValidEntityForSubsumption(parent)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.parent = parent;
    }

    public Entity getChild() {
        return child;
    }

    public void setChild(Entity child) {
        if (!isValidEntityForSubsumption(child)) {
            throw new MetamodelDefinitionCompromisedException(SUBSUMPTION_DEFINITION_ERROR);
        }
        this.child = child;
    }

    public CompletenessConstraint getCompleteness() {
        return completeness;
    }

    public void setCompleteness(CompletenessConstraint completeness) {
        this.completeness = completeness;
    }

    public DisjointObjectType getDisjointness() {
        return disjointness;
    }

    public void setDisjointness(DisjointObjectType disjointness) {
        this.disjointness = disjointness;
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
     * English verbalisation for a Subsumption
     */
    public void toCNLen() {
    	this.cnl.setSubject("Each " + child.getName());
      	this.cnl.setVerb("is");
      	this.cnl.setObject("a " + parent.getName());
      	
      	if (getDisjointness() != null) {
      		getDisjointness().toCNLen(parent.getName());
      	}
      	
      	if (getCompleteness() != null) {
      		getCompleteness().toCNLen(parent.getName());
      	}
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

    /**
     * Generates a JSONObject that represents the information of the Subsumption according to the
     * UML language. The JSONObject generated respects the UML Schema.
     *
     * @return JSONObject that represents the equivalent UML Generalization.
     */
    @Override
    public JSONObject toUML() {
        JSONObject jsonObject = new JSONObject();

        // For now, the representation of Relationship/Role subsumptions will remain the same as Object types subsumptions
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_PARENT, this.parent.getName());
        jsonObject.put(KEY_TYPE, KEY_GENERALIZATION);

        // Classes involved
        JSONArray jsonClasses = new JSONArray();
        jsonClasses.add(this.child.getName());
        jsonObject.put(KEY_CLASSES, jsonClasses);

        // Constraints
        JSONArray jsonConstraints = new JSONArray();
        if (disjointness != null) {
            jsonConstraints.add(DISJOINT_STRING);
        }

        if (completeness != null) {
            jsonConstraints.add(COVERING_STRING);
        }

        jsonObject.put(KEY_CONSTRAINT, jsonConstraints);

        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Subsumption according to the
     * ORM language. The JSONObject generated respects the ORM Schema.
     *
     * @return JSONObject that represents the equivalent ORM Subtyping.
     */
    @Override
    public JSONObject toORM() {
        JSONObject jsonObject = new JSONObject();

        if (parent.getClass().equals(Relationship.class) || parent.getClass().equals(Role.class)) {
            JSONArray parents = new JSONArray();
            JSONArray children = new JSONArray();
            JSONArray parentsPosition = new JSONArray();
            JSONArray childrenPosition = new JSONArray();

            String parentName = parent.getName();
            String childName = child.getName();

            if (parent.getClass().equals(Relationship.class)) {
                parentsPosition.add(CENTER_STRING);
            } else {
                // TODO: Implement a precise way to identify whether the position is right or left
                parentName = ((Role) parent).getRelationship().getName();
                if (parentName.contains("role0")) {
                    parentsPosition.add(LEFT_STRING);
                } else {
                    parentsPosition.add(RIGHT_STRING);
                }
            }

            if (child.getClass().equals(Relationship.class)) {
                childrenPosition.add(CENTER_STRING);
            } else {
                // TODO: Implement a precise way to identify whether the position is right or left
                childName = ((Role) child).getRelationship().getName();
                if (childName.contains("role0")) {
                    childrenPosition.add(LEFT_STRING);
                } else {
                    childrenPosition.add(RIGHT_STRING);
                }
            }

            parents.add(parentName);
            children.add(childName);

            jsonObject.put(KEY_NAME, this.name);
            jsonObject.put(KEY_FACT_PARENT, parents);
            jsonObject.put(KEY_FACT_TYPES, children);
            jsonObject.put(KEY_FACT_PARENT_POSITION, parentsPosition);
            jsonObject.put(KEY_FACT_TYPES_POSITION, childrenPosition);
            jsonObject.put(KEY_TYPE, KEY_ROLE_CONSTRAINT);
            jsonObject.put(KEY_ROLE_CONSTRAINT, SUBSET_CONSTRAINT);
        } else {
            jsonObject.put(KEY_NAME, this.name);
            jsonObject.put(KEY_PARENT, this.parent.getName());
            jsonObject.put(KEY_TYPE, SUBTYPING_STRING);

            // Classes involved
            JSONArray jsonClasses = new JSONArray();
            jsonClasses.add(this.child.getName());
            jsonObject.put(KEY_ENTITIES, jsonClasses);

            // Constraints
            JSONArray jsonConstraints = new JSONArray();
            if (disjointness != null) {
                jsonConstraints.add(EXCLUSIVE_STRING);
            }

            if (completeness != null) {
                jsonConstraints.add(UNION_STRING);
            }

            jsonObject.put(KEY_SUBTYPING_CONSTRAINT, jsonConstraints);
        }
        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Subsumption according to the
     * EER language. The JSONObject generated respects the EER Schema.
     *
     * @return JSONObject that represents the equivalent EER isa.
     */
    @Override
    public JSONObject toEER() {
        JSONObject jsonObject = new JSONObject();
        // For now, the representation of Relationship/Role subsumptions will remain the same as Object types subsumptions
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_PARENT, this.parent.getName());
        jsonObject.put(KEY_TYPE, ISA_STRING);

        // Classes involved
        JSONArray jsonClasses = new JSONArray();
        jsonClasses.add(this.child.getName());
        jsonObject.put(KEY_ENTITIES, jsonClasses);

        // Constraints
        JSONArray jsonConstraints = new JSONArray();
        if (disjointness != null) {
            jsonConstraints.add(EXCLUSIVE_STRING);
        }

        if (completeness != null) {
            jsonConstraints.add(UNION_STRING);
        }

        jsonObject.put(KEY_CONSTRAINT, jsonConstraints);

        return jsonObject;
    }

    /**
     * Entities in a Subsumption can not be Qualified relationships, Attributive properties, Subsumptions, Qualifiers, nor Constraints.
     * Therefore, if the given entity is of the type enlisted, then the entity is not valid.
     *
     * @param entity Entity object to be checked if is valid or not for the Subsumption
     * @return Boolean indicating whether the entity is valid or not.
     */
    private boolean isValidEntityForSubsumption(Entity entity) {
        return !((entity.getClass() == QualifiedRelationship.class)
                || (entity.getClass() == AttributiveProperty.class)
                || (entity.getClass() == Subsumption.class)
                || (entity.getClass() == Qualifier.class)
                || (entity.getClass() == Constraint.class));
    }
}
