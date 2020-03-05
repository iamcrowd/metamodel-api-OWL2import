package com.gilia.metamodel.constraint.cardinality

import com.gilia.exceptions.CardinalityRangeException
import org.apache.commons.lang3.StringUtils
import org.json.simple.JSONObject
import spock.lang.Specification

import static com.gilia.utils.Constants.CARDINALITY_RANGE_ERROR
import static com.gilia.utils.Constants.KEY_CONSTRAINT
import static com.gilia.utils.Constants.KEY_MAXIMUM
import static com.gilia.utils.Constants.KEY_MINIMUM
import static com.gilia.utils.Constants.KEY_NAME

class ObjectTypeCardinalityTest extends Specification {

    void 'ObjectTypeCardinality creation with a cardinality string 0..1'() {
        given:
        String cardinality = "0..1"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "0"
        newConstraint.getMaxCardinality() == "1"
    }

    void 'ObjectTypeCardinality creation with a cardinality string 0..*'() {
        given:
        String cardinality = "0..*"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "0"
        newConstraint.getMaxCardinality() == "*"
    }

    void 'ObjectTypeCardinality creation with a cardinality string 0..456'() {
        given:
        String cardinality = "0..456"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "0"
        newConstraint.getMaxCardinality() == "456"
    }

    void 'ObjectTypeCardinality creation with a cardinality string 1..1'() {
        given:
        String cardinality = "1..1"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "1"
        newConstraint.getMaxCardinality() == "1"
    }

    void 'ObjectTypeCardinality creation with a cardinality string 1..*'() {
        given:
        String cardinality = "1..*"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "1"
        newConstraint.getMaxCardinality() == "*"
    }

    void 'ObjectTypeCardinality creation with a cardinality string *..*'() {
        given:
        String cardinality = "*..*"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "*"
        newConstraint.getMaxCardinality() == "*"
    }

    void 'ObjectTypeCardinality creation with a cardinality string N..*'() {
        given:
        String cardinality = "N..*"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "*"
        newConstraint.getMaxCardinality() == "*"
    }

    void 'ObjectTypeCardinality creation with a cardinality string N..M'() {
        given:
        String cardinality = "N..M"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        noExceptionThrown()
        newConstraint.isNameless()
        newConstraint.getId() != null
        newConstraint.getMinCardinality() == "*"
        newConstraint.getMaxCardinality() == "*"
    }

    void 'ObjectTypeCardinality creation with a cardinality string 2..1'() {
        given:
        String cardinality = "2..1"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        CardinalityRangeException exception = thrown()
        exception.message == "MetamodelException - CardinalityRangeException - " + CARDINALITY_RANGE_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " "
    }

    void 'ObjectTypeCardinality creation with a cardinality string N..1'() {
        given:
        String cardinality = "N..1"

        when:
        ObjectTypeCardinality newConstraint = new ObjectTypeCardinality(cardinality)

        then:
        CardinalityRangeException exception = thrown()
        exception.message == "MetamodelException - CardinalityRangeException - " + CARDINALITY_RANGE_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " "
    }

    void 'Reflexive equality'() {
        when:
        ObjectTypeCardinality cardinality = new ObjectTypeCardinality("0..1");

        then:
        cardinality.equals(cardinality)
    }

    void 'Symmetry equality'() {
        when:
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("0..1");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("0..1");

        then:
        firstCardinality.equals(secondCardinality)
        secondCardinality.equals(firstCardinality)
    }

    void 'Transitive equality'() {
        when:
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("0..1");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("0..1");
        ObjectTypeCardinality thirdCardinality = new ObjectTypeCardinality("0..1");

        then:
        firstCardinality.equals(secondCardinality)
        secondCardinality.equals(thirdCardinality)
        firstCardinality.equals(thirdCardinality)
    }

    void 'Different cardinalities'() {
        when:
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("0..1");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("1..1");

        then:
        !firstCardinality.equals(secondCardinality)
    }

    void 'Equivalent cardinalities'() {
        when:
        ObjectTypeCardinality firstCardinality = new ObjectTypeCardinality("0..*");
        ObjectTypeCardinality secondCardinality = new ObjectTypeCardinality("0..N");

        then:
        firstCardinality.equals(secondCardinality)
    }

    void 'toJSONObject'() {
        given:
        ObjectTypeCardinality newCardinality = new ObjectTypeCardinality("MyNewCardinality", "0..1")

        when:
        JSONObject cardinalityJSON = newCardinality.toJSONObject()

        then:
        cardinalityJSON.containsKey(KEY_NAME)
        cardinalityJSON.get(KEY_NAME) == "MyNewCardinality"
        cardinalityJSON.containsKey(KEY_MINIMUM)
        cardinalityJSON.get(KEY_MINIMUM) == "0"
        cardinalityJSON.containsKey(KEY_MAXIMUM)
        cardinalityJSON.get(KEY_MAXIMUM) == "1"
    }

}
