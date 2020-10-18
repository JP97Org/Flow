package org.jojo.flow.model.api;

import java.util.Arrays;

public enum PinOrientation {
    UP, LEFT, DOWN, RIGHT;
    
    public static PinOrientation of(final String name) {
        return Arrays.stream(values()).filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }
}
