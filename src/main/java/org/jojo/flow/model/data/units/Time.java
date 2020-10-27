package org.jojo.flow.model.data.units;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Fraction;

/**
 * This class represents a time.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic numeric parameter
 */
public class Time<T extends Number> extends Unit<T> {
    /**
     * 
     */
    private static final long serialVersionUID = 4881491437821223647L;

    /**
     * Creates a new time.
     * 
     * @param type - the {@link Unit.Type}
     * @param value - the value
     */
    public Time(final Type type, final T value) {
        super(type, value, UnitSignature.SECOND);
    }
    
    /**
     * Gets a {@link Time} instance represented by the given {@link Unit} instance.
     * If the given unit is not a time, an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param <K> - the given numeric generic parameter
     * @param unit - the given unit
     * @return a {@link Time} instance represented by the given {@link Unit} instance 
     * or {@code null} if the given unit is not a time
     */
    public static <K extends Number> Time<K> of(final Unit<K> unit) {
        if (unit.unit.equals(UnitSignature.SECOND)) {
            return new Time<K>(unit.type, unit.value);
        } else {
            new Warning(null, "given unit is not a time", true).reportWarning();
            return null;
        }
    }
    
    /**
     * Gets a constant.
     * 
     * @param fraction - the constant value
     * @return a constant
     */
    public static Time<Fraction> getFractionConstant(final Fraction fraction) {
        return new Time<>(Type.FRACTION, fraction);
    }
    
    /**
     * Gets a constant.
     * 
     * @param value - the constant value
     * @return a constant
     */
    public static Time<Double> getDoubleConstant(final double value) {
        return new Time<>(Type.DOUBLE, value);
    }
}
