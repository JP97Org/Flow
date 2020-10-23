package org.jojo.flow.model.data.units;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Fraction;

public class Time<T extends Number> extends Unit<T> {
    /**
     * 
     */
    private static final long serialVersionUID = 4881491437821223647L;

    public Time(final Type type, final T value) {
        super(type, value, UnitSignature.SECOND);
    }
    
    public static <K extends Number> Time<K> of(final Unit<K> unit) {
        if (unit.unit.equals(UnitSignature.SECOND)) {
            return new Time<K>(unit.type, unit.value);
        } else {
            new Warning(null, "given unit is not a time", true).reportWarning();
            return null;
        }
    }
    
    public static Time<Fraction> getFractionConstant(final Fraction fraction) {
        return new Time<>(Type.FRACTION, fraction);
    }
    
    public static Time<Double> getDoubleConstant(final double value) {
        return new Time<>(Type.DOUBLE, value);
    }
}
