package com.gilia.metamodel.constraint

import com.gilia.metamodel.entitytype.objecttype.ObjectType
import spock.lang.Specification

class CompletenessConstraintTest extends Specification {

    void 'Creation with name'() {
        when:
        CompletenessConstraint newConstraint = new CompletenessConstraint("MyCompletenessConstraint")

        then:
        noExceptionThrown()
        newConstraint.getId() != null
        newConstraint.getName() == "MyCompletenessConstraint"
        newConstraint.getEntities() == null
    }

    void 'Creation with entities'() {
        when:
        ObjectType newEntity = new ObjectType("MyEntity")
        ArrayList<ObjectType> entities = new ArrayList()
        entities.add(newEntity)
        CompletenessConstraint newConstraint = new CompletenessConstraint(entities)

        then:
        noExceptionThrown()
        newConstraint.getId() != null
        newConstraint.isNameless()
        newConstraint.getEntities() == entities
    }

    void 'Reflexive equality'() {
        when:
        ObjectType firstEntity = new ObjectType("firstEntity")
        ObjectType secondEntity = new ObjectType("secondEntity")
        ArrayList<ObjectType> entities = new ArrayList()
        entities.add(firstEntity)
        entities.add(secondEntity)
        CompletenessConstraint newConstraint = new CompletenessConstraint("MyConstraint", entities)

        then:
        newConstraint.equals(newConstraint)
    }

    void 'Symmetry equality'() {
        when:
        ObjectType firstEntity = new ObjectType("firstEntity")
        ObjectType secondEntity = new ObjectType("secondEntity")
        ArrayList<ObjectType> entities = new ArrayList()
        entities.add(firstEntity)
        entities.add(secondEntity)
        CompletenessConstraint firstConstraint = new CompletenessConstraint("newConstraint", entities)
        CompletenessConstraint secondConstraint = new CompletenessConstraint("newConstraint", entities)

        then:
        firstConstraint.equals(secondConstraint)
        secondConstraint.equals(firstConstraint)
    }

    void 'Transitive equality'() {

    }
}
