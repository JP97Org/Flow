package org.jojo.flow.model.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public abstract class DataSignature implements Iterable<DataSignature>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5112402497719036749L;
    protected static final int UNKNOWN = -100;
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
    
    private boolean isHashEfficient;
    
    public DataSignature(final int dataId) {
        this.dataId = dataId;
        this.isHashEfficient = false;
    }
    
    private DataSignature(final int dataId, final boolean isHashEfficient) {
        this(dataId);
        this.isHashEfficient = isHashEfficient;
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
    
    public DataSignature tryGetHashEfficientCopy() {
        final DataSignature copy = getCopy();
        final DataSignature ret = new DataSignature(this.dataId, copy.isCheckingRecursive()) {
            /**
             * 
             */
            private static final long serialVersionUID = -5593591045946032816L;

            @Override
            public DataSignature getCopy() {
                return copy.getCopy(); // loses hash efficiency
            }

            @Override
            public DataSignature getComponent(final int index) {
                return copy.getComponent(index);
            }

            @Override
            public int size() {
                return copy.size();
            }

            @Override
            public String toString() {
                return copy.toString();
            }

            @Override
            public DataSignature ofString(final String info) {
                return copy.ofString(info); // loses hash efficiency
            }
            
            @Override
            public int hashCode() {
                if (isCheckingRecursive()) {
                    final DataSignature[] components = getComponents();
                    return Objects.hash(getDataId(), Arrays.deepHashCode(components));
                } else {
                    return copy.hashCode();
                }
            }
            
            @Override
            public boolean equals(final Object other) {
                if (isCheckingRecursive()) {
                    if (other != null && other instanceof DataSignature) {
                        final DataSignature otherSig = (DataSignature)other;
                        final DataSignature[] components = getComponents();
                        final DataSignature[] otherComponents = otherSig.getComponents();
                        return copy.dataId == otherSig.dataId && Arrays.deepEquals(components, otherComponents);
                    }
                    return false;
                }
                return copy.equals(other);
            }
        };
        return ret;
    }
    
    public boolean isRecursiveSignature() {
        return this.dataId == BUNDLE || this.dataId == ARRAY || this.dataId == VECTOR;
    }
    
    public boolean isSingleTypeRecursiveSignature() {
        return isRecursiveSignature() && this.dataId != BUNDLE;
    }
    
    public DataSignature deactivateChecking() {
        this.dataId = DONT_CARE;
        return this;
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
    
    public boolean isHashEfficient() {
        return isCheckingRecursive() && this.isHashEfficient;
    }
    
    public abstract DataSignature getComponent(final int index);
    
    public abstract int size();
    
    public DataSignature[] getComponents() {
        final DataSignature[] components = new DataSignature[size()];
        for (int i = 0; i < size(); i++) {
            components[i] = getComponent(i);
        }
        return components;
    }
    
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
    
    protected String getNameOfDataId() {
        if (getDataId() == DONT_CARE) {
            return "not checking";
        } else if (getDataId() == NO_SIZES) {
            return "no_sizes";
        }
        
        switch(getDataId()) {
            case BASIC_COMPONENT_SIZES: return "sizes";
            case BASIC_COMPONENT_TYPE: return "type";
            case BASIC_COMPONENT_UNIT: return "unit";
            default:
                if (getDataId() >= BASIC_COMPONENT_SIZE_0) {
                    return "one size int, index= " + (getDataId() - BASIC_COMPONENT_SIZE_0);
                }
                final Class<?> dataClass = getDataClass();
                return dataClass == null ? "unknown" : dataClass.getName();
        }
    }
    
    private static int getDataIdOfName(final String name) {
        if (name.equals("not checking")) {
            return DONT_CARE;
        } else if (name.equals("no_sizes")) {
            return NO_SIZES;
        }
        
        switch(name) {
            case "sizes": return BASIC_COMPONENT_SIZES;
            case "type": return BASIC_COMPONENT_TYPE;
            case "unit": return BASIC_COMPONENT_UNIT;
            default:
                if (name.equals(ScalarDataSet.class.getName())) {
                    return SCALAR;
                } else if (name.equals(Matrix.class.getName())) {
                    return MATRIX;
                } else if (name.equals(StringDataSet.class.getName())) {
                    return STRING;
                } else if (name.equals(DataBundle.class.getName())) {
                    return BUNDLE;
                } else if (name.equals(DataVector.class.getName())) {
                    return VECTOR;
                } else if (name.equals(DataArray.class.getName())) {
                    return ARRAY;
                } else if (name.equals(MathMatrix.class.getName())) {
                    return MATH_MATRIX;
                } else if (name.equals(Tensor.class.getName())) {
                    return TENSOR;
                } else if (name.equals(MultiMatrix.class.getName())) {
                    return MULTI_MATRIX;
                } else if (name.equals(RawDataSet.class.getName())) {
                    return RAW;
                }
                
                final String[] split = name.split("= ");
                try {
                    final int id = split.length == 2 ? Integer.parseInt(split[1]) + BASIC_COMPONENT_SIZE_0 : UNKNOWN;
                    return id;
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException(nfe.toString());
                }
        }
    }
    
    private static DataSignature createDataSignatureByIdAndInfo(final int id, final String info) {
        final Integer[][] matrix = {{1}};
        final Unit<Integer> unit = Unit.getIntegerConstant(0);
        final UnitSignature unitSign = UnitSignature.NO_UNIT;
        final Data[] data = new Data[0];
        final DataSignature sign = new StringDataSet("").getDataSignature();
        final byte[] b = new byte[]{0};
        final int[] in = new int[] {NO_SIZES};
        final BasicType bt = BasicType.INT;
        if (id >= 0 && id != BASIC_COMPONENT_UNIT && (info == null || info.equals(""))) {
            return null;
        }
        try {
            switch (id) {
                case NO_SIZES: return new SizesDataSignature.OneSizeDataSignature(NO_SIZES, 0);
                case DONT_CARE: return new DontCareDataSignature().ofString(info);
                case SCALAR: return new BasicSignature(new ScalarDataSet<Integer>(unit)).ofString(info);
                case MATRIX: return new BasicSignature(new Matrix<Integer>(matrix, unitSign)).ofString(info);
                case STRING: return new BasicSignature(new StringDataSet("")).ofString(info);
                case BUNDLE: return new RecursiveSignature(new DataBundle(data)).ofString(info);
                case VECTOR: return new RecursiveSignature(new DataVector(Arrays.asList(data), sign)).ofString(info);
                case ARRAY: return new RecursiveSignature(new DataArray(data, sign)).ofString(info);
                case MATH_MATRIX: return new BasicSignature(new MathMatrix<Integer>(matrix, unitSign)).ofString(info);
                case RAW: return new BasicSignature(new RawDataSet(b)).ofString(info);
                case BASIC_COMPONENT_SIZES: return new SizesDataSignature(in).ofString(info);
                case BASIC_COMPONENT_TYPE: return new BasicTypeDataSignature(bt).ofString(info);
                case BASIC_COMPONENT_UNIT: return new UnitDataSignature(unitSign).ofString(info);
                default: 
                    if (id >= BASIC_COMPONENT_SIZE_0) {
                        final int index = id - BASIC_COMPONENT_SIZE_0;
                        return new SizesDataSignature.OneSizeDataSignature(NO_SIZES, index).ofString(info);
                    }
                    return null;
            }
        } catch (DataTypeIncompatException e) {
            //should not happen
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public abstract String toString();
    
    public abstract DataSignature ofString(final String info);
    
    public static DataSignature of(final String string) {
        final String name = string.replaceFirst("\\s\\|.*", "");
        final String info = string.replaceFirst("[^|]*\\|\\s", "");
        final int id = getDataIdOfName(name);
        return createDataSignatureByIdAndInfo(id, info);
    }

    public Iterator<DataSignature> iterator() {
        return Arrays.asList(getComponents()).iterator();
    }
    
    protected static class DontCareDataSignature extends DataSignature {
        /**
         * 
         */
        private static final long serialVersionUID = -1401990759544006890L;
        private String info;
        
        public DontCareDataSignature() {
            super(DONT_CARE);
            this.info = "";
        }
        
        @Override
        public DataSignature getCopy() {
            final DontCareDataSignature ret = new DontCareDataSignature();
            ret.info = info;
            return ret;
        }

        @Override
        public DataSignature getComponent(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String toString() {
            return toStringDs() + this.info;
        }

        @Override
        public DataSignature ofString(final String info) {
            this.info = info;
            return getCopy();
        }
    }
}
