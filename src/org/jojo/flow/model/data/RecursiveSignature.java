package org.jojo.flow.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecursiveSignature extends DataSignature {
    private final List<DataSignature> components;
    
    public RecursiveSignature(/*TODO recursive checkable*/) {
        super(0); //TODO DataID of recursive checkable
        this.components = new ArrayList<>();
        // TODO set components to that from recursive checkable
    }
    
    private RecursiveSignature(final RecursiveSignature toCopy) {
        super(toCopy.getDataId());
        this.components = toCopy.components.stream()
                .map(x -> x.getCopy())
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.components.toArray(), ((RecursiveSignature) other).components.toArray());
        }
        return false;
    }

    @Override
    public DataSignature getCopy() {
        return new RecursiveSignature(this);
    }

    @Override
    public DataSignature getComponent(int index) {
        return this.components.get(index);
    }

    @Override
    public int size() {
        return this.components.size();
    }
}
