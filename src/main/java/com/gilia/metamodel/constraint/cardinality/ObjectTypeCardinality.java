package com.gilia.metamodel.constraint.cardinality;

import com.gilia.exceptions.CardinalityRangeException;
import com.gilia.exceptions.CardinalitySyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Objects;

import static com.gilia.utils.Constants.*;

/**
 * Representation of the ObjectTypeCardinality class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class ObjectTypeCardinality extends CardinalityConstraint {

    private String minCardinality;
    private String maxCardinality;

    public ObjectTypeCardinality(String cardinality) throws CardinalitySyntaxException, CardinalityRangeException {
        super();
        if (cardinality.matches(CARDINALITY_REGEX)) {
            String[] cardinalities = cardinality.split(CARDINALITY_DIVIDER_REGEX);
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

    public ObjectTypeCardinality(String name, String cardinality) throws CardinalitySyntaxException {
        super(name);
        if (cardinality.matches(CARDINALITY_REGEX)) {
            String[] cardinalities = cardinality.split(CARDINALITY_DIVIDER_REGEX);
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

    public ObjectTypeCardinality(String name, String minCardinality, String maxCardinality) {
        super(name);
        if (minCardinality.matches(CARDINALITY_LEFT_COMPONENT_REGEX) && maxCardinality.matches(CARDINALITY_RIGHT_COMPONENT_REGEX)) {
            String newMinCardinality = minCardinality.equals("N") ? "*" : minCardinality;
            String newMaxCardinality = maxCardinality.equals("N") || maxCardinality.equals("M") ? "*" : maxCardinality;
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

    public JSONObject toJSONObject() {
        JSONObject objectTypeCardinalityConstraint = new JSONObject();

        objectTypeCardinalityConstraint.put(KEY_NAME, name);
        objectTypeCardinalityConstraint.put(KEY_MINIMUM, minCardinality);
        objectTypeCardinalityConstraint.put(KEY_MAXIMUM, maxCardinality);

        return objectTypeCardinalityConstraint;
    }

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
