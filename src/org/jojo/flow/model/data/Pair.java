package org.jojo.flow.model.data;

public class Pair<T1, T2> {
    public final T1 first;
    public final T2 second;

    public Pair(final T1 first, final T2 second) {
        this.first = first;
        this.second = second;
    }    
}
