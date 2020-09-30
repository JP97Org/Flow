package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Objects;

public class SizesDataSignature extends BasicSignatureComponentSignature {
    private final int[] sizes;
    
    public SizesDataSignature(final int[] sizes) {
        super(BASIC_COMPONENT_SIZES);
        this.sizes = Objects.requireNonNull(sizes);
    }

    @Override
    public DataSignature getCopy() {
        return new SizesDataSignature(Arrays.stream(sizes).toArray());
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.sizes, ((SizesDataSignature) other).sizes);
        }
        return false;
    }
}
