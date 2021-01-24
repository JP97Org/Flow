package org.jojo.flow.model.api;

import static org.jojo.flow.model.api.Unit.Operation.*;
import static org.jojo.flow.model.api.Unit.Type.*;
import static org.jojo.flow.model.api.UnitSignature.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import org.jojo.flow.exc.IllegalUnitOperationException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.data.Fraction;

/**
 * This class represents a unit scalar, i.e. a numeric value and an {@link UnitSignature}.
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic numeric type
 */
public class Unit<T extends Number> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 2402866882045663314L;

    /**
     * This enum contains constants for the different allowed basic numeric types.
     * 
     * @author Jonathan Schenkenberger
     * @version 1.0
     * @see BasicType
     */
    public enum Type {
        BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIG_INT, BIG_DECIMAL, FRACTION;

        /**
         * Transforms the given value to the correct type defined by this enum constant.
         * 
         * @param value - the value
         * @return the number with the correct type
         */
        public Number transformToCorrectType(final int value) {
            switch (this) {
            case BIG_DECIMAL:
                return new BigDecimal("" + value);
            case BIG_INT:
                return new BigInteger("" + value);
            case BYTE:
                return (byte) value;
            case DOUBLE:
                return (double) value;
            case FLOAT:
                return (float) value;
            case INT:
                return (int) value;
            case LONG:
                return (long) value;
            case SHORT:
                return (short) value;
            case FRACTION:
                return new Fraction(value);
            default:
                assert false;
                return null;
            }
        }
        
        /**
         * Transforms the given value to the correct type defined by this enum constant.
         * 
         * @param value - the value
         * @return the number with the correct type
         */
        public Number transformToCorrectType(final double value) {
            switch (this) {
            case BIG_DECIMAL:
                return new BigDecimal(value);
            case BIG_INT:
                return new BigInteger("" + value);
            case BYTE:
                return (byte) value;
            case DOUBLE:
                return (double) value;
            case FLOAT:
                return (float) value;
            case INT:
                return (int) value;
            case LONG:
                return (long) value;
            case SHORT:
                return (short) value;
            case FRACTION:
                return new Fraction(value);
            default:
                assert false;
                return null;
            }
        }
    }
    
    /**
     * Operation Enum for internal calculations.
     * 
     * @author Jonathan Schenkenberger
     * @version 1.0
     */
    protected enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE;
    }
    
    public static final Unit<Integer> ZERO = new Unit<Integer>(INT, 0, NO_UNIT);
    public static final Unit<Integer> ONE = new Unit<Integer>(INT, 1, NO_UNIT);
    
    public static final Unit<BigDecimal> ATTO  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.000000000000000001"), NO_UNIT);
    public static final Unit<BigDecimal> FEMTO = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.000000000000001"), NO_UNIT);
    public static final Unit<BigDecimal> PICO  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.000000000001"), NO_UNIT);
    public static final Unit<BigDecimal> NANO  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.000000001"), NO_UNIT);
    public static final Unit<BigDecimal> MICRO = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.000001"), NO_UNIT);
    public static final Unit<BigDecimal> MILLI = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.001"), NO_UNIT);
    public static final Unit<BigDecimal> CENTI = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.01"), NO_UNIT);
    public static final Unit<BigDecimal> DECI  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("0.1"), NO_UNIT);

    public static final Unit<BigDecimal> DECA  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("10.0"), NO_UNIT);
    public static final Unit<BigDecimal> HECTO = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("100.0"), NO_UNIT);
    public static final Unit<BigDecimal> KILO  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000.0"), NO_UNIT);
    public static final Unit<BigDecimal> MEGA  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000000.0"), NO_UNIT);
    public static final Unit<BigDecimal> GIGA  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000000000.0"), NO_UNIT);
    public static final Unit<BigDecimal> TERA  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000000000000.0"), NO_UNIT);
    public static final Unit<BigDecimal> PETA  = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000000000000000.0"), NO_UNIT);
    public static final Unit<BigDecimal> EXA   = new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal("1000000000000000000.0"), NO_UNIT);

    public static final Unit<Double> DEGREE = new Unit<Double>(DOUBLE, Math.PI / 180., NO_UNIT);
    public static final Unit<Double> GON = new Unit<Double>(DOUBLE, Math.PI / 200., NO_UNIT);
    public static final Unit<Double> ARCMINUTE = DEGREE.operateSafely(DIVIDE, getDoubleConstant(60.));
    public static final Unit<Double> ARCSECOND = ARCMINUTE.operateSafely(DIVIDE, getDoubleConstant(60.));
    
    // SI base units
    public static final Unit<Integer> SI_LENGTH = ONE.multiply(METER);
    public static final Unit<Integer> SI_MASS = ONE.multiply(KILOGRAMM);
    public static final Unit<Integer> SI_TIME = ONE.multiply(SECOND);
    public static final Unit<Integer> SI_CURRENT = ONE.multiply(AMPERE);
    public static final Unit<Integer> SI_TEMPERATURE = ONE.multiply(KELVIN);
    public static final Unit<Integer> SI_AMOUNT_OF_SUBSTANCE = ONE.multiply(MOLE);
    public static final Unit<Integer> SI_LUMINOUS_INTESITY = ONE.multiply(CANDELA);

    // other SI units
    public static final Unit<Integer> SI_FORCE = ONE.multiply(NEWTON);
    public static final Unit<Integer> SI_TORQUE = ONE.multiply(NEWTON_METER);
    public static final Unit<Integer> SI_PRESSURE = ONE.multiply(PASCAL);
    public static final Unit<Integer> SI_ENERGY = ONE.multiply(JOULE);
    public static final Unit<Integer> SI_POWER = ONE.multiply(WATT);
    public static final Unit<Integer> SI_VOLTAGE = ONE.multiply(VOLT);
    public static final Unit<Integer> SI_RESISTANCE = ONE.multiply(OHM);
    public static final Unit<Integer> SI_CONDUCTANCE = ONE.multiply(SIEMENS);
    public static final Unit<Integer> SI_INDUCTANCE = ONE.multiply(HENRY);
    public static final Unit<Integer> SI_MAGNETIC_FIELD_STRENGTH = ONE.multiply(TESLA);
    public static final Unit<Integer> SI_CAPACITANCE = ONE.multiply(FARAD);
    public static final Unit<Integer> SI_FREQUENCY = ONE.multiply(HERTZ);
    public static final Unit<Integer> SI_CHARGE = ONE.multiply(COULOMB);
    public static final Unit<Integer> SI_MAGNETIC_FLUX = ONE.multiply(WEBER);
    public static final Unit<Integer> SI_RADIATION_DOSE = ONE.multiply(GRAY);
    public static final Unit<Integer> SI_ILLUMINANCE = ONE.multiply(LUX);
    public static final Unit<Integer> SI_CATALYTIC_ACTIVITY = ONE.multiply(KATAL);
    public static final Unit<Integer> SI_AREA = ONE.multiply(SQUARE_METER);
    public static final Unit<Integer> SI_VOLUME = ONE.multiply(CUBE_METER);
    public static final Unit<Integer> SI_SPEED = ONE.multiply(METER_PER_SECOND);
    public static final Unit<Integer> SI_ANGULAR_SPEED = ONE.multiply(RADIAN_PER_SECOND);
    public static final Unit<Integer> SI_ACCELERATION = ONE.multiply(METER_PER_SQUARE_SECOND);
    public static final Unit<Integer> SI_JERK = ONE.multiply(METER_PER_CUBE_SECOND);
    public static final Unit<Integer> SI_JOUNCE = ONE.multiply(METER_PER_TESSERACT_SECOND);
    
    // non-SI units
    public static final Unit<Double> INCH = getDoubleConstant(0.0254).multiply(METER);
    public static final Unit<Double> FOOT = getDoubleConstant(0.3048).multiply(METER);
    public static final Unit<Double> YARD = getDoubleConstant(0.9144).multiply(METER);
    public static final Unit<Double> MILE = getDoubleConstant(1609.34).multiply(METER);
    public static final Unit<Integer> NAUTICAL_MILE = getIntegerConstant(1852).multiply(METER);
    public static final Unit<Double> GRAMM = getDoubleConstant(0.001).multiply(KILOGRAMM);
    public static final Unit<Double> OUNZE = getDoubleConstant(0.0283495).multiply(KILOGRAMM);
    public static final Unit<Double> TON_SHORT = getDoubleConstant(907.18474).multiply(KILOGRAMM);
    public static final Unit<Integer> TON_METRIC = getIntegerConstant(1000).multiply(KILOGRAMM);
    public static final Unit<Integer> MINUTE = getIntegerConstant(60).multiply(SECOND);
    public static final Unit<Integer> HOUR = getIntegerConstant(3600).multiply(SECOND);
    public static final Unit<Integer> DAY = getIntegerConstant(24 * 3600).multiply(SECOND);
    public static final Unit<Integer> WEEK = getIntegerConstant(7 * 24 * 3600).multiply(SECOND);
    public static final Unit<Integer> MONTH_BANK = getIntegerConstant(30 * 7 * 24 * 3600).multiply(SECOND);
    public static final Unit<Integer> YEAR_NO_LEAP = getIntegerConstant(365 * 7 * 24 * 3600).multiply(SECOND);
    public static final Unit<Integer> BAR = getIntegerConstant(100000).multiply(PASCAL);
    public static final Unit<Integer> ATMOSPERE = getIntegerConstant(101325).multiply(PASCAL);
    public static final Unit<BigDecimal> ERG = getBigDecimalConstant(100.).operateSafely(MULTIPLY, NANO).multiply(JOULE);
    public static final Unit<Double> HP = getDoubleConstant(735.49875).multiply(WATT);
    public static final Unit<Double> RAD = getDoubleConstant(0.01).multiply(GRAY);
    public static final Unit<Integer> HECTARE = getIntegerConstant(10000).multiply(SQUARE_METER);
    public static final Unit<Double> LITRE = getDoubleConstant(0.001).multiply(CUBE_METER);
    public static final Unit<Double> GALLONS_US = getDoubleConstant(3.785411784).operateSafely(MULTIPLY, LITRE);
    public static final Unit<Double> FLUID_OUNCE_US = GALLONS_US.operateSafely(DIVIDE, getDoubleConstant(128.0));
    public static final Unit<Double> GALLONS_UK = getDoubleConstant(4.54609).operateSafely(MULTIPLY, LITRE);
    public static final Unit<Integer> C = getIntegerConstant(299792458).multiply(METER_PER_SECOND);
    public static final Unit<Double> G = getDoubleConstant(9.80665).multiply(METER_PER_SQUARE_SECOND);
    
    /**
     * The basic numeric type of this unit.
     * 
     * @see Unit.Type
     */
    public final Type type;
    
    /**
     * The value.
     */
    public final T value;
    
    /**
     * The unit signature.
     * 
     * @see UnitSignature
     */
    public final UnitSignature unit;
    
    /**
     * Creates a new unit with the given type, value and unit signature.
     * 
     * @param type - the Unit.Type
     * @param value - the value
     * @param unit - the unit signature
     */
    public Unit(final Type type, final T value, final UnitSignature unit) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this.unit = Objects.requireNonNull(unit);
        
        if (!BasicType.of(value).equals(type)) {
            throw new IllegalArgumentException("value has type= " + BasicType.of(value) + " but declared was type= " + type);
        }
    }
    
    /**
     * Copy-Constructor.
     * 
     * @param toCopy
     */
    public Unit(final Unit<T> toCopy) {
        Objects.requireNonNull(toCopy);
        this.type = toCopy.type;
        this.value = toCopy.value;
        this.unit = toCopy.unit;
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<Integer> getIntegerConstant(final int value) {
        return new Unit<Integer>(INT, value, NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<Long> getLongConstant(final long value) {
        return new Unit<Long>(INT, value, NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<BigInteger> getBigIntConstant(final BigInteger value) {
        return new Unit<BigInteger>(BIG_INT, value, NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<Double> getDoubleConstant(final double value) {
        return new Unit<Double>(DOUBLE, value, NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<Fraction> getFractionConstant(final Fraction value) {
        return new Unit<Fraction>(FRACTION, value, NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<BigDecimal> getBigDecimalConstant(final double value) {
        return new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal(value), NO_UNIT);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant
     * @return the constant value as a unit
     */
    public static Unit<BigDecimal> getBigDecimalConstant(final BigDecimal value) {
        return new Unit<BigDecimal>(BIG_DECIMAL, value, NO_UNIT);
    }
    
    private Unit<T> operateSafely(final Operation operation, Unit<T> other) {
        try {
            switch(operation) {
                case ADD: return add(other);
                case SUBTRACT: return subtract(other);
                case MULTIPLY: return multiply(other);
                case DIVIDE:  return divide(other);
                default: assert false; return null;
            }
        } catch (IllegalArgumentException | IllegalUnitOperationException e) {
            // should not happen
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adds the other unit.
     * 
     * @param other - the other unit
     * @return the sum
     * @throws IllegalUnitOperationException if units do not fit
     */
    public Unit<T> add(final Unit<T> other) throws IllegalUnitOperationException {
        if (this.type == other.type) {
            final Type type = this.type;
            final Number value;
            switch (type) {
                case BYTE: 
                    value = (byte) (this.value.byteValue() + other.value.byteValue());
                    break;
                case SHORT:
                    value = (short) (this.value.shortValue() + other.value.shortValue());
                    break;
                case INT:
                    value = this.value.intValue() + other.value.intValue();
                    break;
                case LONG:
                    value = this.value.longValue() + other.value.longValue();
                    break;
                case FLOAT:
                    value = this.value.floatValue() + other.value.floatValue();
                    break;
                case DOUBLE:
                    value = this.value.doubleValue() + other.value.doubleValue();
                    break;
                case BIG_INT:
                    if (this.value instanceof BigInteger && other.value instanceof BigInteger) {
                        value = ((BigInteger) this.value).add((BigInteger) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case BIG_DECIMAL:
                    if (this.value instanceof BigDecimal && other.value instanceof BigDecimal) {
                        value = ((BigDecimal) this.value).add((BigDecimal) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case FRACTION:
                    if (this.value instanceof Fraction && other.value instanceof Fraction) {
                        value = (Fraction) ((IFraction) this.value).add((IFraction) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }
                    break;
                default:
                    value = null;
                    assert false;
            }
            if (this.value.getClass().isInstance(value)) {
                @SuppressWarnings("unchecked") // it is checked
                final T tVal = (T) value;
                return new Unit<T>(type, tVal, unit.add(other.unit));
            } else {
                throw new IllegalUnitOperationException("actual number type does not match declared type");
            }
        } else {
            throw new IllegalUnitOperationException("type " + this.type + " and " + other.type + " are not equal");
        }
    }
    
    /**
     * Subtracts the other unit.
     * 
     * @param other - the other unit
     * @return the difference
     * @throws IllegalUnitOperationException if units do not fit
     */
    public Unit<T> subtract(final Unit<T> other) throws IllegalUnitOperationException {
        if (this.type == other.type) {
            final Type type = this.type;
            final Number value;
            switch (type) {
                case BYTE: 
                    value = (byte) (this.value.byteValue() - other.value.byteValue());
                    break;
                case SHORT:
                    value = (short) (this.value.shortValue() - other.value.shortValue());
                    break;
                case INT:
                    value = this.value.intValue() - other.value.intValue();
                    break;
                case LONG:
                    value = this.value.longValue() - other.value.longValue();
                    break;
                case FLOAT:
                    value = this.value.floatValue() - other.value.floatValue();
                    break;
                case DOUBLE:
                    value = this.value.doubleValue() - other.value.doubleValue();
                    break;
                case BIG_INT:
                    if (this.value instanceof BigInteger && other.value instanceof BigInteger) {
                        value = ((BigInteger) this.value).subtract((BigInteger) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case BIG_DECIMAL:
                    if (this.value instanceof BigDecimal && other.value instanceof BigDecimal) {
                        value = ((BigDecimal) this.value).subtract((BigDecimal) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case FRACTION:
                    if (this.value instanceof Fraction && other.value instanceof Fraction) {
                        value = (Fraction) ((IFraction) this.value).subtract((IFraction) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }
                    break;
                default:
                    value = null;
                    assert false;
            }
            if (this.value.getClass().isInstance(value)) {
                @SuppressWarnings("unchecked") // it is checked
                final T tVal = (T) value;
                return new Unit<T>(type, tVal, unit.subtract(other.unit));
            } else {
                throw new IllegalUnitOperationException("actual number type does not match declared type");
            }
        } else {
            throw new IllegalUnitOperationException("type " + this.type + " and " + other.type + " are not equal");
        }
    }
    
    /**
     * Multiplies the other unit.
     * 
     * @param other - the other unit
     * @return the product
     * @throws IllegalUnitOperationException if units do not fit
     */
    public Unit<T> multiply(final Unit<T> other) throws IllegalUnitOperationException {
        if (this.type == other.type) {
            final Type type = this.type;
            final Number value;
            switch (type) {
                case BYTE: 
                    value = (byte) (this.value.byteValue() * other.value.byteValue());
                    break;
                case SHORT:
                    value = (short) (this.value.shortValue() * other.value.shortValue());
                    break;
                case INT:
                    value = this.value.intValue() * other.value.intValue();
                    break;
                case LONG:
                    value = this.value.longValue() * other.value.longValue();
                    break;
                case FLOAT:
                    value = this.value.floatValue() * other.value.floatValue();
                    break;
                case DOUBLE:
                    value = this.value.doubleValue() * other.value.doubleValue();
                    break;
                case BIG_INT:
                    if (this.value instanceof BigInteger && other.value instanceof BigInteger) {
                        value = ((BigInteger) this.value).multiply((BigInteger) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case BIG_DECIMAL:
                    if (this.value instanceof BigDecimal && other.value instanceof BigDecimal) {
                        value = ((BigDecimal) this.value).multiply((BigDecimal) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case FRACTION:
                    if (this.value instanceof Fraction && other.value instanceof Fraction) {
                        value = (Fraction) ((IFraction) this.value).multiply((IFraction) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }
                    break;
                default:
                    value = null;
                    assert false;
            }
            if (this.value.getClass().isInstance(value)) {
                @SuppressWarnings("unchecked") // it is checked
                final T tVal = (T) value;
                return new Unit<T>(type, tVal, unit.multiply(other.unit));
            } else {
                throw new IllegalUnitOperationException("actual number type does not match declared type");
            }
        } else {
            throw new IllegalUnitOperationException("type " + this.type + " and " + other.type + " are not equal");
        }
    }
    
    /**
     * Multiplies the given unit signature.
     * 
     * @param otherUnit - other unit signature
     * @return the product of this unit and the given unit signature
     */
    public Unit<T> multiply(final UnitSignature otherUnit) {
        return new Unit<T>(this.type, this.value, this.unit.multiply(otherUnit));
    }
    
    /**
     * Divides by the other unit.
     * 
     * @param other - the other unit (must have 0 value)
     * @return the division result
     * @throws IllegalUnitOperationException if units do not fit
     * @throws IllegalArgumentException if division by 0 occurs
     */
    public Unit<T> divide(final Unit<T> other) throws IllegalUnitOperationException, IllegalArgumentException {
        if (this.type == other.type) {
            final Type type = this.type;
            final Number value;
            if(other.value.doubleValue() == 0) {
            	throw new IllegalArgumentException("division by 0 is not allowed");
            }
            switch (type) {
                case BYTE: 
                    value = (byte) (this.value.byteValue() / other.value.byteValue());
                    break;
                case SHORT:
                    value = (short) (this.value.shortValue() / other.value.shortValue());
                    break;
                case INT:
                    value = this.value.intValue() / other.value.intValue();
                    break;
                case LONG:
                    value = this.value.longValue() / other.value.longValue();
                    break;
                case FLOAT:
                    value = this.value.floatValue() / other.value.floatValue();
                    break;
                case DOUBLE:
                    value = this.value.doubleValue() / other.value.doubleValue();
                    break;
                case BIG_INT:
                    if (this.value instanceof BigInteger && other.value instanceof BigInteger) {
                        value = ((BigInteger) this.value).divide((BigInteger) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case BIG_DECIMAL:
                    if (this.value instanceof BigDecimal && other.value instanceof BigDecimal) {
                        value = ((BigDecimal) this.value).divide((BigDecimal) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }  
                    break;
                case FRACTION:
                    if (this.value instanceof Fraction && other.value instanceof Fraction) {
                        value = (Fraction) ((IFraction) this.value).divide((IFraction) other.value);
                    } else {
                        throw new IllegalUnitOperationException("actual number type does not match declared type");
                    }
                    break;
                default:
                    value = null;
                    assert false;
            }
            if (this.value.getClass().isInstance(value)) {
                @SuppressWarnings("unchecked") // it is checked
                final T tVal = (T) value;
                return new Unit<T>(type, tVal, unit.divide(other.unit));
            } else {
                throw new IllegalUnitOperationException("actual number type does not match declared type");
            }
        } else {
            throw new IllegalUnitOperationException("type " + this.type + " and " + other.type + " are not equal");
        }
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Byte> toByteUnit() {
        return new Unit<Byte>(BYTE, this.value.byteValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Short> toShortUnit() {
        return new Unit<Short>(SHORT, this.value.shortValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Integer> toIntUnit() {
        return new Unit<Integer>(INT, this.value.intValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Long> toLongUnit() {
        return new Unit<Long>(LONG, this.value.longValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<BigInteger> toBigIntUnit() {
        return new Unit<BigInteger>(BIG_INT, new BigInteger("" + this.value), this.unit);
    }

    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Float> toFloatUnit() {
        return new Unit<Float>(FLOAT, this.value.floatValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Double> toDoubleUnit() {
        return new Unit<Double>(DOUBLE, this.value.doubleValue(), this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<BigDecimal> toBigDecimalUnit() {
        return new Unit<BigDecimal>(BIG_DECIMAL, 
                this.value instanceof BigDecimal ? (BigDecimal)this.value : new BigDecimal(this.value.doubleValue()),
                        this.unit);
    }
    
    /**
     * Gets the unit representing this unit but with the type mentioned in the name of the method.
     * 
     * @return the unit representing this unit but with the type mentioned in the name of the method
     */
    public Unit<Fraction> toFractionUnit() {
        if(this.type == Type.FRACTION) {
            @SuppressWarnings("unchecked")
            final Unit<Fraction> ret = (Unit<Fraction>) this;
            return ret;
        } else if (this.type == Type.BYTE || this.type == Type.SHORT || this.type == Type.INT || this.type == Type.LONG) {
            return new Unit<Fraction>(FRACTION, new Fraction(this.value.longValue(), 1L), this.unit);
        }
        return new Unit<Fraction>(FRACTION, new Fraction(this.value.doubleValue()), this.unit);
    }
    
    /**
     * Gets the celsius value of this temperature.
     * 
     * @return the celsius value of this temperature
     * @throws UnsupportedOperationException if this unit is not a temperature
     */
    public double toCelsius() throws UnsupportedOperationException {
        if (!this.unit.equals(UnitSignature.KELVIN)) {
            throw new UnsupportedOperationException("only temperatures can be converted to another temperature unit");
        }
        return toDoubleUnit().operateSafely(SUBTRACT, new Unit<>(Unit.Type.DOUBLE, 273.15, this.unit)).value;
    }
    
    /**
     * Gets the fahrenheit value of this temperature.
     * 
     * @return the fahrenheit value of this temperature
     * @throws UnsupportedOperationException if this unit is not a temperature
     */
    public double toFahrenheit() {
        if (!this.unit.equals(UnitSignature.KELVIN)) {
            throw new UnsupportedOperationException("only temperatures can be converted to another temperature unit");
        }
        return toDoubleUnit()
                .operateSafely(MULTIPLY, new Unit<>(Unit.Type.DOUBLE, 1.8, UnitSignature.NO_UNIT))
                .operateSafely(SUBTRACT, new Unit<>(Unit.Type.DOUBLE, 459.67, this.unit)).value;
    }
    
    /**
     * Transforms a celsius value to a fahrenheit value.
     * 
     * @param celsiusValue - the celsius value
     * @return the fahrenheit value
     */
    public static double fromCelsiusToFahrenheit(final double celsiusValue) {
        return getDoubleConstant(celsiusValue + 273.15).multiply(UnitSignature.KELVIN).toFahrenheit();
    }
    
    /**
     * Transforms a fahrenheit value to a celsius value.
     * 
     * @param fahrenheitValue - the fahrenheit value
     * @return the celsius value
     */
    public static double fromFahrenheitToCelsius(final double fahrenheitValue) {
        return getDoubleConstant((fahrenheitValue + 459.67) * (5./9.)).multiply(UnitSignature.KELVIN).toCelsius();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.value, this.unit);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Unit) {
            final Unit<?> otherUnit = (Unit<?>) other;
            return this.type.equals(otherUnit.type) 
                    && this.value.equals(otherUnit.value) 
                    && this.unit.equals(otherUnit.unit);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return this.value + " " + this.unit;
    }
    
    /**
     * Gets a more pretty representation.
     * 
     * @return  a more pretty representation
     */
    public String toPrettyString() {
        return toPrettyString(ONE, true, false);
    }
    
    /**
     * Gets a more pretty representation using the given prefix.
     * 
     * @param prefix - the given prefix
     * @return a more pretty representation using the given prefix
     */
    public String toPrettyString(final Unit<?> prefix) {
        return toPrettyString(prefix, true, false);
    }
    
    /**
     * Gets a more pretty representation using the given prefix.
     * 
     * @param prefix - the given prefix
     * @param isEnergy - whether J or Nm should be printed for energy/torque values
     * @param isRadian - whether this unit is an angle
     * @return a more pretty representation using the given prefix
     */
    public String toPrettyString(final Unit<?> prefix, final boolean isEnergy, final boolean isRadian) {
        String ret = toString();
        if (prefix == null || ONE.equals(prefix)) {
            ret = this.value + " " + this.unit.toPrettyString(isEnergy, isRadian);
        } else {
            if (prefix.equals(ATTO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, EXA);
                ret = unit.value + " " + "a(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(FEMTO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, PETA);
                ret = unit.value + " " + "f(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(PICO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, TERA);
                ret = unit.value + " " + "p(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(NANO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, GIGA);
                ret = unit.value + " " + "n(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(MICRO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, MEGA);
                ret = unit.value + " " + '\u00B5' + "(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(MILLI)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, KILO);
                ret = unit.value + " " + "m(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(CENTI)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, HECTO);
                ret = unit.value + " " + "c(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(DECI)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, DECA);
                ret = unit.value + " " + "d(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(DECA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, DECI);
                ret = unit.value + " " + "da(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(HECTO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, CENTI);
                ret = unit.value + " " + "h(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(KILO)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, MILLI);
                ret = unit.value + " " + "k(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(MEGA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, MICRO);
                ret = unit.value + " " + "M(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(GIGA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, NANO);
                ret = unit.value + " " + "G(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(TERA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, PICO);
                ret = unit.value + " " + "T(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(PETA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, FEMTO);
                ret = unit.value + " " + "P(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            } else if (prefix.equals(EXA)) {
                final Unit<BigDecimal> unit = this.toBigDecimalUnit().operateSafely(Operation.MULTIPLY, ATTO);
                ret = unit.value + " " + "E(" + this.unit.toPrettyString(isEnergy, isRadian) + ")";
            }
        }
        return ret;
    }
}
