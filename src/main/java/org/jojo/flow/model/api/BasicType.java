package org.jojo.flow.model.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * The BasicType enum represents a basic type for the IData IBasicCheckable datatypes.
 * A BasicType is one of these: 
 * boolean, char, byte, short, int, long, float, double, BigInteger, BigDecimal or Fraction.
 * Moreover, there is also an UNKNOWN constant for unknown datatypes, i.e none of the beforementioned types.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum BasicType {
    BOOL, CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIG_INT, BIG_DECIMAL, FRACTION, UNKNOWN;

    /**
     * 
     * @param value - the numeric value
     * @return  the Unit.Type representing the given numeric value or {@code null} if it is {@code null} 
     *          or not one of the following:
     *          byte, short, int, long, float, double, BigInteger, BigDecimal or Fraction
     */
    public static Unit.Type of(final Number value) {
        if (value instanceof Byte) {
            return Unit.Type.BYTE;
        }
        if (value instanceof Short) {
            return Unit.Type.SHORT;
        }
        if (value instanceof Integer) {
            return Unit.Type.INT;
        }
        if (value instanceof Long) {
            return Unit.Type.LONG;
        }
        if (value instanceof Float) {
            return Unit.Type.FLOAT;
        }
        if (value instanceof Double) {
            return Unit.Type.DOUBLE;
        }
        if (value instanceof BigInteger) {
            return Unit.Type.BIG_INT;
        }
        if (value instanceof BigDecimal) {
            return Unit.Type.BIG_DECIMAL;
        }
        if (value instanceof IFraction) {
            return Unit.Type.FRACTION;
        }
        return null;
    }

    /**
     * 
     * @param type - the unit type (must not be null)
     * @return the basic type representing the given Unit.Type
     */
    public static BasicType of(final Unit.Type type) {
        Objects.requireNonNull(type);
        return Arrays.stream(values())
                .filter(x -> x.name().equals(type.name()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 
     * @param o - the given object
     * @return the respective basic type of the object or BasicType.UNKNOWN if it is unknown
     */
    public static BasicType of(final Object o) {
        if (o instanceof Boolean) {
            return BOOL;
        }
        if (o instanceof Character) {
            return CHAR;
        }
        
        return o instanceof Number ? of(of((Number)o)) : UNKNOWN;
    }
    
    /**
     * 
     * @param toStrRes - the toString result of a BasicType enum constant
     * @return the respective basic type or {@code null} if none matches
     */
    public static BasicType of(final String toStrRes) {
        return Arrays.stream(values()).filter(b -> b.toString().equals(toStrRes)).findFirst().orElse(null);
    }
}
