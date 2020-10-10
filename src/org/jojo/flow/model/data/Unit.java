package org.jojo.flow.model.data;

import static org.jojo.flow.model.data.UnitSignature.*;
import static org.jojo.flow.model.data.Unit.Type.*;
import static org.jojo.flow.model.data.Unit.Operation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class Unit<T extends Number> {
    public enum Type {
        BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIG_INT, BIG_DECIMAL, FRACTION;

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
    public static final Unit<Double> PS = getDoubleConstant(735.49875).multiply(WATT);
    public static final Unit<Double> RAD = getDoubleConstant(0.01).multiply(GRAY);
    public static final Unit<Integer> HECTARE = getIntegerConstant(10000).multiply(SQUARE_METER);
    public static final Unit<Double> LITRE = getDoubleConstant(0.001).multiply(CUBE_METER);
    public static final Unit<Double> GALLONS_US = getDoubleConstant(3.785411784).operateSafely(MULTIPLY, LITRE);
    public static final Unit<Double> FLUID_OUNCE_US = GALLONS_US.operateSafely(DIVIDE, getDoubleConstant(128.0));
    public static final Unit<Double> GALLONS_UK = getDoubleConstant(4.54609).operateSafely(MULTIPLY, LITRE);
    public static final Unit<Integer> C = getIntegerConstant(299792458).multiply(METER_PER_SECOND);
    public static final Unit<Double> G = getDoubleConstant(9.80665).multiply(METER_PER_SQUARE_SECOND);
    
    public final Type type;
    
    public final T value;
    public final UnitSignature unit;
    
    public Unit(final Type type, final T value, final UnitSignature unit) {
        this.type = type;
        this.value = value;
        this.unit = unit;
    }
    
    public Unit(final Unit<T> toCopy) {
        this.type = toCopy.type;
        this.value = toCopy.value;
        this.unit = toCopy.unit;
    }
    
    public static Unit<Integer> getIntegerConstant(final int value) {
        return new Unit<Integer>(INT, value, NO_UNIT);
    }
    
    public static Unit<Double> getDoubleConstant(final double value) {
        return new Unit<Double>(DOUBLE, value, NO_UNIT);
    }
    
    public static Unit<Fraction> getFractionConstant(final Fraction value) {
        return new Unit<Fraction>(FRACTION, value, NO_UNIT);
    }
    
    public static Unit<BigDecimal> getBigDecimalConstant(final double value) {
        return new Unit<BigDecimal>(BIG_DECIMAL, new BigDecimal(value), NO_UNIT);
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
        } catch (ArithmeticException | IllegalUnitOperationException e) {
            // should not happen
            e.printStackTrace();
            return null;
        }
    }
    
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
                        value = ((Fraction) this.value).add((Fraction) other.value);
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
                        value = ((Fraction) this.value).subtract((Fraction) other.value);
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
                        value = ((Fraction) this.value).multiply((Fraction) other.value);
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
    
    public Unit<T> multiply(final UnitSignature otherUnit) {
        return new Unit<T>(this.type, this.value, this.unit.multiply(otherUnit));
    }
    
    public Unit<T> divide(final Unit<T> other) throws IllegalUnitOperationException, ArithmeticException {
        if (this.type == other.type) {
            final Type type = this.type;
            final Number value;
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
                        value = ((Fraction) this.value).divide((Fraction) other.value);
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
    
    public Unit<Byte> toByteUnit() {
        return new Unit<Byte>(BYTE, this.value.byteValue(), this.unit);
    }
    
    public Unit<Short> toShortUnit() {
        return new Unit<Short>(SHORT, this.value.shortValue(), this.unit);
    }
    
    public Unit<Integer> toIntUnit() {
        return new Unit<Integer>(INT, this.value.intValue(), this.unit);
    }
    
    public Unit<Long> toLongUnit() {
        return new Unit<Long>(LONG, this.value.longValue(), this.unit);
    }
    
    public Unit<BigInteger> toBigIntUnit() {
        return new Unit<BigInteger>(BIG_INT, new BigInteger("" + this.value), this.unit);
    }
    
    public Unit<Float> toFloatUnit() {
        return new Unit<Float>(FLOAT, this.value.floatValue(), this.unit);
    }
    
    public Unit<Double> toDoubleUnit() {
        return new Unit<Double>(DOUBLE, this.value.doubleValue(), this.unit);
    }
    
    public Unit<BigDecimal> toBigDecimalUnit() {
        return new Unit<BigDecimal>(BIG_DECIMAL, 
                this.value instanceof BigDecimal ? (BigDecimal)this.value : new BigDecimal(this.value.doubleValue()),
                        this.unit);
    }
    
    public Unit<Fraction> toFractionUnit() {
        if(this.type == Type.FRACTION) {
            @SuppressWarnings("unchecked")
            final Unit<Fraction> ret = (Unit<Fraction>) this;
            return ret;
        } else if (this.type == Type.BYTE || this.type == Type.SHORT || this.type == Type.INT || this.type == Type.LONG) {
            return new Unit<Fraction>(FRACTION, new Fraction(this.value.longValue(), 1), this.unit);
        }
        return new Unit<Fraction>(FRACTION, new Fraction(this.value.doubleValue()), this.unit);
    }
    
    public double toCelsius() throws UnsupportedOperationException {
        if (!this.unit.equals(UnitSignature.KELVIN)) {
            throw new UnsupportedOperationException("only temperatures can be converted to another temperature unit");
        }
        return toDoubleUnit().operateSafely(SUBTRACT, new Unit<>(Unit.Type.DOUBLE, 273.15, this.unit)).value;
    }
    
    public double toFahrenheit() {
        if (!this.unit.equals(UnitSignature.KELVIN)) {
            throw new UnsupportedOperationException("only temperatures can be converted to another temperature unit");
        }
        return toDoubleUnit()
                .operateSafely(MULTIPLY, new Unit<>(Unit.Type.DOUBLE, 1.8, UnitSignature.NO_UNIT))
                .operateSafely(SUBTRACT, new Unit<>(Unit.Type.DOUBLE, 459.67, this.unit)).value;
    }
    
    public static double fromCelsiusToFahrenheit(final double celsiusValue) {
        return getDoubleConstant(celsiusValue + 273.15).multiply(UnitSignature.KELVIN).toFahrenheit();
    }
    
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
    
    public String toPrettyString() {
        return toPrettyString(ONE, true, false);
    }
    
    public String toPrettyString(final Unit<?> prefix) {
        return toPrettyString(prefix, true, false);
    }
    
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
