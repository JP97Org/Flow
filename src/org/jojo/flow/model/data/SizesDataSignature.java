package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Objects;

public class SizesDataSignature extends BasicSignatureComponentSignature {
    /**
     * 
     */
    private static final long serialVersionUID = 6554802350914594510L;
    private final int[] sizes;
    private final OneSizeDataSignature[] sizesSignatures;
    
    public SizesDataSignature(final int[] sizes) {
        super(BASIC_COMPONENT_SIZES);
        this.sizes = Objects.requireNonNull(sizes);
        this.sizesSignatures = new OneSizeDataSignature[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            this.sizesSignatures[i] = new OneSizeDataSignature(sizes[i], i);
        }
    }
    
    @Override
    public DataSignature getComponent(final int index) {
        return this.sizesSignatures[index];
    }
    
    @Override
    public int size() {
        return this.sizesSignatures.length;
    }

    @Override
    public DataSignature getCopy() {
        return new SizesDataSignature(Arrays.stream(this.sizes).toArray());
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            if (!isChecking() || !((DataSignature)other).isChecking()) {
                return true;
            }
            return Arrays.equals(this.sizes, ((SizesDataSignature) other).sizes);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return toStringDs() + Arrays.toString(this.sizes).replaceAll(",", ";;");
    }
    
    @Override
    public DataSignature ofString(final String info) {
        final String prepared = info.substring(1, info.length() - 1);
        try {
            final int[] split = Arrays.stream(prepared.split(";;\\s")).mapToInt(s -> Integer.parseInt(s)).toArray();
            return new SizesDataSignature(split);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected static class OneSizeDataSignature extends BasicSignatureComponentSignature {
        /**
         * 
         */
        private static final long serialVersionUID = 296930622432811086L;
        private final int dataIDOffset;
        private final int size;
        
        OneSizeDataSignature(final int size, final int dataIDOffset) {
            super(BASIC_COMPONENT_SIZE_0 + dataIDOffset);
            this.dataIDOffset = dataIDOffset;
            this.size = size;
        }

        @Override
        public DataSignature getCopy() {
            return new OneSizeDataSignature(this.size, this.dataIDOffset);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.dataIDOffset, this.size);
        }
        
        @Override
        public boolean equals(final Object other) {
            if (super.equals(other)) {
                return this.size == ((OneSizeDataSignature) other).size;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return toStringDs() + this.size;
        }
        
        @Override
        public DataSignature ofString(final String info) {
            try {
                return new OneSizeDataSignature(Integer.parseInt(info), getDataId() - BASIC_COMPONENT_SIZE_0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
