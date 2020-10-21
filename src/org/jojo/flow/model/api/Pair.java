package org.jojo.flow.model.api;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class represents a pair.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @param <T1> - generic type of first value
 * @param <T2> - generic type of second value
 */
public class Pair<T1, T2> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6469925928903861976L;
    
    /**
     * The first value.
     */
    public final T1 first;
    
    /**
     * The second value.
     */
    public final T2 second;

    /**
     * Creates a new pair with the given values.
     * 
     * @param first - the first value
     * @param second - the second value
     */
    public Pair(final T1 first, final T2 second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] {this.first, this.second});
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Pair) {
            final Pair<?, ?> otherPair = (Pair<?, ?>)other;
            return Arrays.deepEquals(new Object[] {this.first, this.second}
                    , new Object[] {otherPair.first, otherPair.second});
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ")";
    }
}
