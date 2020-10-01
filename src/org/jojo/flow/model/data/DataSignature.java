package org.jojo.flow.model.data;

public abstract class DataSignature {
    protected static final int NO_SIZES = -2;
    protected static final int DONT_CARE = -1;
    protected static final int SCALAR = 0;
    protected static final int MATRIX = 1;
    protected static final int STRING = 2;
    protected static final int BUNDLE = 3;
    protected static final int VECTOR = 4;
    protected static final int ARRAY = 5;
    protected static final int MATH_MATRIX = 6;
    protected static final int TENSOR = 7;
    protected static final int MULTI_MATRIX = 8;
    protected static final int RAW = 9;

    //Basic component signatures
    protected static final int BASIC_COMPONENT_SIZES = 0x100;
    protected static final int BASIC_COMPONENT_TYPE = 0x101;
    protected static final int BASIC_COMPONENT_UNIT = 0x102;

    //0. size signature (all DataIDs >= this DataID are reserved for sizes-dimensions)
    protected static final int BASIC_COMPONENT_SIZE_0 = 0x1000;
    
    private int dataId;
    
    public DataSignature(final int dataId) {
        this.dataId = dataId;
    }
    
    @Override
    public int hashCode() {
        // a constant number since ucid may be DONT_CARE,
        // so it may be that a.equals(b) is independent of the ucid
        return 1; 
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof DataSignature) {
            final DataSignature otherSig = (DataSignature)other;
            return this.dataId == otherSig.dataId || !isChecking() || !otherSig.isChecking();
        }
        return false;
    }
    
    public abstract DataSignature getCopy();
    
    public boolean isRecursiveSignature() {
        return this.dataId == BUNDLE || this.dataId == ARRAY || this.dataId == VECTOR;
    }
    
    public boolean isSingleTypeRecursiveSignature() {
        return isRecursiveSignature() && this.dataId != BUNDLE;
    }
    
    public void deactivateChecking() {
        this.dataId = DONT_CARE;
    }
    
    public boolean isChecking() {
        return this.dataId != DONT_CARE;
    }
    
    public boolean isCheckingRecursive() {
        boolean ret = isChecking();
        
        for (int i = 0; ret && i < size(); i++) {
            ret = getComponent(i) == null || getComponent(i).isCheckingRecursive();
        }
        
        return ret;
    }
    
    public abstract DataSignature getComponent(final int index);
    
    public abstract int size();
    
    protected int getDataId() {
        return this.dataId;
    }
    
    public Class<?> getDataClass() {
        switch(getDataId()) {
            case SCALAR: return ScalarDataSet.class;
            case MATRIX: return Matrix.class;
            case STRING: return StringDataSet.class;
            case BUNDLE: return DataBundle.class;
            case VECTOR: return DataVector.class;
            case ARRAY: return DataArray.class;
            case MATH_MATRIX: return MathMatrix.class;
            case TENSOR: return Tensor.class;
            case MULTI_MATRIX: return MultiMatrix.class;
            case RAW: return RawDataSet.class;
            case BASIC_COMPONENT_SIZES: return int[].class;
            case BASIC_COMPONENT_TYPE: return BasicType.class;
            case BASIC_COMPONENT_UNIT: return UnitSignature.class;
            default: 
                if (getDataId() >= BASIC_COMPONENT_SIZE_0) {
                    return int.class;
                }
            }
        return null;
    }

    protected String toStringDs() {
        return "" + getNameOfDataId() + " | ";
    }
    
    private String getNameOfDataId() {
        switch(getDataId()) {
            case BASIC_COMPONENT_SIZES: return "sizes";
            default:
                if (getDataId() >= BASIC_COMPONENT_SIZE_0) {
                    return "one size int";
                }
                final Class<?> dataClass = getDataClass();
                return dataClass == null ? "unknown" : dataClass.getName();
        }
    }

    @Override
    public abstract String toString();
}
