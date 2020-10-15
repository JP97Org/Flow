package org.jojo.flow.model.data.units;

import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.Unit;
import org.jojo.flow.model.data.UnitSignature;

public class Frequency<T extends Number> extends Unit<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -6150564293073786328L;

    public Frequency(final Type type, final T value) {
        super(type, value, UnitSignature.HERTZ);
    }
    
    public static <K extends Number> Frequency<K> of(final Unit<K> unit) {
        if (unit.unit.equals(UnitSignature.HERTZ)) {
            return new Frequency<K>(unit.type, unit.value);
        } else {
            return null; //TODO maybe exc
        }
    }
    
    public static Frequency<Fraction> getFractionConstant(final Fraction fraction) {
        return new Frequency<>(Type.FRACTION, fraction);
    }
}
