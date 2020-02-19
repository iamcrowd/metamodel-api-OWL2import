package com.gilia.metamodel.constraint.cardinality;

import com.gilia.exceptions.CardinalityRangeException;
import com.gilia.exceptions.CardinalitySyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the Object Type Cardinality class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class ObjectTypeCardinality extends CardinalityConstraint {

    private String minCardinality;
    private String maxCardinality;

    public ObjectTypeCardinality(String cardinality) throws CardinalitySyntaxException, CardinalityRangeException {
        super();
        validateCardinalityString(cardinality);
    }

    public ObjectTypeCardinality(String name, String cardinality) throws CardinalitySyntaxException {
        super(name);
        validateCardinalityString(cardinality);
    }

    public ObjectTypeCardinality(String name, String minCardinality, String maxCardinality) {
        super(name);
        String cardinality = minCardinality + ".." + maxCardinality;
        validateCardinalityString(cardinality);
    }

    public String getMinCardinality() {
        return minCardinality;
    }

    public void setMinCardinality(String minCardinality) {
        if (minCardinality.matches(CARDINALITY_LEFT_COMPONENT_REGEX)) {
            String newMinCardinality = minCardinality.equals("N") ? "*" : minCardinality;
            if (isCardinalityRangeValid(newMinCardinality, this.maxCardinality)) {
                this.minCardinality = newMinCardinality;
            } else {
                throw new CardinalityRangeException(CARDINALITY_RANGE_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
            }
        } else {
            throw new CardinalitySyntaxException(CARDINALITY_SYNTAX_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
        }
    }

    public String getMaxCardinality() {
        return maxCardinality;
    }

    public void setMaxCardinality(String maxCardinality) {
        if (maxCardinality.matches(CARDINALITY_RIGHT_COMPONENT_REGEX)) {
            String newMaxCardinality = maxCardinality.equals("N") ? "*" : maxCardinality;
            if (isCardinalityRangeValid(this.minCardinality, maxCardinality)) {
                this.maxCardinality = newMaxCardinality;
            } else {
                throw new CardinalityRangeException(CARDINALITY_RANGE_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
            }
        } else {
            throw new CardinalitySyntaxException(CARDINALITY_SYNTAX_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
        }
    }

    /**
     * Returns a string that represents the cardinality according to the CARDINALITY_REGEX
     *
     * @return A string that represents a cardinality range.
     * @see com.gilia.utils.Constants#CARDINALITY_REGEX
     */
    public String getCardinality() {
        return minCardinality + ".." + maxCardinality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ObjectTypeCardinality that = (ObjectTypeCardinality) o;
        return Objects.equals(minCardinality, that.minCardinality) &&
                Objects.equals(maxCardinality, that.maxCardinality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minCardinality, maxCardinality);
    }

    @Override
    public String toString() {
        return "ObjectTypeCardinality{" +
                "minCardinality='" + minCardinality + '\'' +
                ", maxCardinality='" + maxCardinality + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public JSONObject toJSONObject() {
        JSONObject objectTypeCardinalityConstraint = new JSONObject();

        objectTypeCardinalityConstraint.put(KEY_NAME, name);
        objectTypeCardinalityConstraint.put(KEY_MINIMUM, minCardinality);
        objectTypeCardinalityConstraint.put(KEY_MAXIMUM, maxCardinality);

        return objectTypeCardinalityConstraint;
    }

    /**
     * Parses a cardinality string, validating it and instantiating the minCardinality and maxCardinality fields.
     * The string will be valid if it respects the CARDINALITY_REGEX and if the ranges are valid (min <= max)
     *
     * @param cardinalityString A string that represents a cardinality (min..max).
     * @throws CardinalitySyntaxException
     * @throws CardinalityRangeException
     * @see com.gilia.metamodel.constraint.cardinality.ObjectTypeCardinality#isCardinalityRangeValid(String, String)
     */
    private void validateCardinalityString(String cardinalityString) throws CardinalitySyntaxException, CardinalityRangeException {
        if (cardinalityString.matches(CARDINALITY_REGEX)) {
            String[] cardinalities = cardinalityString.split(CARDINALITY_DIVIDER_REGEX);
            String newMinCardinality = cardinalities[0].equals("N") ? "*" : cardinalities[0];
            String newMaxCardinality = cardinalities[1].equals("N") || cardinalities[1].equals("M") ? "*" : cardinalities[1];
            if (isCardinalityRangeValid(newMinCardinality, newMaxCardinality)) {
                this.minCardinality = newMinCardinality;
                this.maxCardinality = newMaxCardinality;
            } else {
                throw new CardinalityRangeException(CARDINALITY_RANGE_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
            }
        } else {
            throw new CardinalitySyntaxException(CARDINALITY_SYNTAX_ERROR + " " + StringUtils.capitalize(KEY_CONSTRAINT) + " " + name);
        }
    }

    /**
     * Checks if the cardinality is a valid range. A range is considered valid if the minCardinality is equal or smaller
     * than the maxCardinality
     *
     * @param minCardinality Minimum component from the cardinality
     * @param maxCardinality Maximum component from the cardinality
     * @return A boolean indicating wheter the cardinality range is valid or not
     */
    private boolean isCardinalityRangeValid(String minCardinality, String maxCardinality) {
        int minCardinalityValue, maxCardinalityValue;

        if (minCardinality.equals("*")) {
            minCardinalityValue = Integer.MAX_VALUE;
        } else {
            try {
                minCardinalityValue = Integer.valueOf(minCardinality);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (maxCardinality.equals("*")) {
            maxCardinalityValue = Integer.MAX_VALUE;
        } else {
            try {
                maxCardinalityValue = Integer.valueOf(maxCardinality);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return minCardinalityValue <= maxCardinalityValue;
    }


}
