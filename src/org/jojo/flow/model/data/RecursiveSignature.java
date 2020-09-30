package org.jojo.flow.model.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RecursiveSignature extends DataSignature {
    private final List<DataSignature> components;
    
    public RecursiveSignature(final RecursiveCheckable data) {
        super(data.getDataId());
        this.components = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            this.components.add(data.get(i).getDataSignature());
        }
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
