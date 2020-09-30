package org.jojo.flow.model.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public enum BasicType {
    BOOL, CHAR, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIG_INT, BIG_DECIMAL;

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
        return null;
    }

    public static BasicType of(final Unit.Type type) {
        Objects.requireNonNull(type);
        return Arrays.stream(values())
                .filter(x -> x.name().equals(type.name()))
                .findFirst()
                .orElse(null);
    }
}
