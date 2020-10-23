package org.jojo.flow.model.api;

import java.util.Arrays;

/**
 * This enum represents whether the pin is up, left, down or right of the flow module.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum PinOrientation {
    UP, LEFT, DOWN, RIGHT;
    
    /**
     * Gets the pin orientation for the given name.
     * 
     * @param name - the given name
     * @return the pin orientation for the given name or {@code null} if none matches the name
     */
    public static PinOrientation of(final String name) {
        return Arrays.stream(values()).filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }
}
