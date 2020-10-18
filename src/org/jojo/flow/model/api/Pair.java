package org.jojo.flow.model.api;

import java.io.Serializable;
import java.util.Arrays;

public class Pair<T1, T2> implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6469925928903861976L;
    public final T1 first;
    public final T2 second;

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
}
