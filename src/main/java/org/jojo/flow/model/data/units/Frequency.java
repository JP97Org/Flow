package org.jojo.flow.model.data.units;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;
import org.jojo.flow.model.data.Fraction;

/**
 * This class represents a frequency.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T> - the generic numeric parameter
 */
public class Frequency<T extends Number> extends Unit<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -6150564293073786328L;

    /**
     * Creates a new frequency.
     * 
     * @param type - the {@link Unit.Type}
     * @param value - the value
     */
    public Frequency(final Type type, final T value) {
        super(type, value, UnitSignature.HERTZ);
    }
    
    /**
     * Gets a {@link Frequency} instance represented by the given {@link Unit} instance.
     * If the given unit is not a frequency, an error {@link org.jojo.flow.exc.Warning} is reported.
     * 
     * @param <K> - the given numeric generic parameter
     * @param unit - the given unit
     * @return a {@link Frequency} instance represented by the given {@link Unit} instance 
     * or {@code null} if the given unit is not a frequency
     */
    public static <K extends Number> Frequency<K> of(final Unit<K> unit) {
        if (unit.unit.equals(UnitSignature.HERTZ)) {
            return new Frequency<K>(unit.type, unit.value);
        } else {
            new Warning(null, "given unit is not a frequency", true).reportWarning();
            return null;
        }
    }
    
    /**
     * Gets a constant.
     * 
     * @param fraction - the constant value
     * @return a constant
     */
    public static Frequency<Fraction> getFractionConstant(final Fraction fraction) {
        return new Frequency<>(Type.FRACTION, fraction);
    }
}
