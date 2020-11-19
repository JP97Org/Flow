package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IDataSignature;

class SizesDataSignature extends BasicSignatureComponentSignature {
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
        final SizesDataSignature ret = new SizesDataSignature(Arrays.stream(this.sizes).toArray());
        for (int i = 0; i < this.sizes.length; i++) {
            if (!this.sizesSignatures[i].isChecking()) {
                ret.getComponent(i).deactivateChecking();
            }
        }
        return ret;
    }
    
    @Override
    public String toString() {
        final StringJoiner ret = new StringJoiner(";; ", toStringDs() + "[", "]");
        final String notChecking = new DontCareDataSignature().getNameOfDataId();
        for (int i = 0; i < this.sizes.length ; i++) {
            ret.add(this.sizesSignatures[i].isChecking() ? "" + this.sizes[i] : notChecking);
        }
        return ret.toString();
    }
    
    @Override
    public DataSignature ofString(final String info) {
        final String prepared = info.substring(1, info.length() - 1);
        final String[] splitStr = prepared.split(";;\\s"); 
        final String notChecking = new DontCareDataSignature().getNameOfDataId();
        try {
            final int[] split = Arrays.stream(splitStr)
                    .mapToInt(s -> s.equals(notChecking) ? DONT_CARE : Integer.parseInt(s))
                    .toArray();
            final SizesDataSignature ret = new SizesDataSignature(split);
            for (int i = 0; i < split.length; i++) {
                if (((OneSizeDataSignature)ret.getComponent(i)).size == DONT_CARE) {
                    ret.getComponent(i).deactivateChecking();
                }
            }
            return ret;
        } catch (NumberFormatException e) {
        	new Warning(null, e.toString(), true).reportWarning();
            e.printStackTrace();
            return null;
        }
    }
    
    static class OneSizeDataSignature extends BasicSignatureComponentSignature {
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
        public boolean matches(final IDataSignature other) {
            if (super.matches(other)) {
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
            	new Warning(null, e.toString(), true).reportWarning();
                e.printStackTrace();
                return null;
            }
        }
    }
}
