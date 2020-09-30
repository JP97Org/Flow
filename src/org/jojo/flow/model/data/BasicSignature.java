package org.jojo.flow.model.data;

import java.util.Arrays;

public class BasicSignature extends DataSignature {
    private final BasicSignatureComponentSignature[] components;
    
    public BasicSignature(/*TODO basic checkable*/) {
        super(0); //TODO DataID of basic checkable
        this.components = new BasicSignatureComponentSignature[BasicSignatureComponents.values().length];
        // TODO set components to that from basic checkable
    }
    
    private BasicSignature(final BasicSignature toCopy) {
        super(toCopy.getDataId());
        this.components = Arrays.stream(toCopy.components)
                .map(x -> x.getCopy())
                .toArray(BasicSignatureComponentSignature[]::new);
    }

    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.components, ((BasicSignature) other).components);
        }
        return false;
    }

    @Override
    public DataSignature getCopy() {
        return new BasicSignature(this);
    }

    @Override
    public DataSignature getComponent(int index) {
        return this.components[index];
    }

    @Override
    public int size() {
        return this.components.length;
    }
}
