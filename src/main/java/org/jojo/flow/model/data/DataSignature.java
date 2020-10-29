package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

import org.jojo.flow.exc.DataTypeIncompatException;
import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.IDataSignature;
import org.jojo.flow.model.api.Unit;
import org.jojo.flow.model.api.UnitSignature;

public abstract class DataSignature implements IDataSignature {
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
    
    // other constants
    protected static final String DONT_CARE_STR = "not checking";
    protected static final String NO_SIZES_STR = "no_sizes";
    protected static final String SIZES_STR = "sizes";
    protected static final String TYPE_STR = "type";
    protected static final String UNIT_STR = "unit";
    protected static final String UNKNOWN_STR = "unknown";
    protected static final String ONE_SIZE_STR = "one size int, index";
    protected static final String INDEX_EQSIGN_STR = "= ";
    protected static final String DELIM_STR = " | ";
    protected static final String REPL_REGEX_NAME_STR = "\\s\\|.*"; // see also DELIM_STR
    protected static final String REPL_REGEX_INFO_STR = "[^|]*\\|\\s"; // see also REPL_REGEX_NAME_STR
    
    private int dataId;
    
    public DataSignature(final int dataId) {
        this.dataId = dataId;
    }
    
    @Override
    public synchronized boolean matches(final IDataSignature other) {
        if (other == null && !isChecking()) {
            return true;
        }
        if (other != null) {
            final IDataSignature otherSig = (IDataSignature)other;
            if (!isChecking() || !otherSig.isChecking()) {
                return true;
            }
            
            boolean componentsAllMatch = size() == otherSig.size();
            for (int i = 0; componentsAllMatch && i < size(); i++) {
                if (getComponent(i) == null) {
                    if (otherSig.getComponent(i) != null) {
                        componentsAllMatch &= !otherSig.getComponent(i).isChecking();
                    }
                    continue;
                }
                componentsAllMatch &= getComponent(i).matches(otherSig.getComponent(i));
            }
            return this.dataId == otherSig.getDataId() && componentsAllMatch;
        }
        return false;
    }
    
    @Override
    public abstract IDataSignature getCopy();
    
    @Override
    public boolean isRecursiveSignature() {
        return this.dataId == BUNDLE || this.dataId == ARRAY || this.dataId == VECTOR;
    }
    
    @Override
    public boolean isSingleTypeRecursiveSignature() {
        return isRecursiveSignature() && this.dataId != BUNDLE;
    }
    
    @Override
    public DataSignature deactivateChecking() {
        this.dataId = DONT_CARE;
        return this;
    }
    
    @Override
    public boolean isChecking() {
        return this.dataId != DONT_CARE;
    }
    
    @Override
    public boolean isCheckingRecursive() {
        boolean ret = isChecking();
        
        for (int i = 0; ret && i < size(); i++) {
            ret = getComponent(i) == null || getComponent(i).isCheckingRecursive();
        }
        
        return ret;
    }
    
    @Override
    public abstract IDataSignature getComponent(final int index);
    
    @Override
    public abstract int size();
    
    @Override
    public IDataSignature[] getComponents() {
        final IDataSignature[] components = new DataSignature[size()];
        for (int i = 0; i < size(); i++) {
            components[i] = getComponent(i);
        }
        return components;
    }
    
    @Override
    public int getDataId() {
        return this.dataId;
    }
    
    @Override
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
        return getNameOfDataId() + DELIM_STR;
    }
    
    protected String getNameOfDataId() {
        if (getDataId() == DONT_CARE) {
            return DONT_CARE_STR;
        } else if (getDataId() == NO_SIZES) {
            return NO_SIZES_STR;
        }
        
        switch(getDataId()) {
            case BASIC_COMPONENT_SIZES: return SIZES_STR;
            case BASIC_COMPONENT_TYPE: return TYPE_STR;
            case BASIC_COMPONENT_UNIT: return UNIT_STR;
            default:
                if (getDataId() >= BASIC_COMPONENT_SIZE_0) {
                    return ONE_SIZE_STR + INDEX_EQSIGN_STR + (getDataId() - BASIC_COMPONENT_SIZE_0);
                }
                final Class<?> dataClass = getDataClass();
                return dataClass == null ? UNKNOWN_STR : dataClass.getName();
        }
    }
    
    private static int getDataIdOfName(final String name) {
        if (name.equals(DONT_CARE_STR)) {
            return DONT_CARE;
        } else if (name.equals(NO_SIZES_STR)) {
            return NO_SIZES;
        }
        
        switch(name) {
            case SIZES_STR: return BASIC_COMPONENT_SIZES;
            case TYPE_STR: return BASIC_COMPONENT_TYPE;
            case UNIT_STR: return BASIC_COMPONENT_UNIT;
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
                
                final String[] split = name.split(INDEX_EQSIGN_STR);
                final int id = split.length == 2 
                        ? Integer.parseInt(split[1]) + BASIC_COMPONENT_SIZE_0 : UNKNOWN;
                return id;
        }
    }
    
    private static IDataSignature createDataSignatureByIdAndInfo(final int id, final String info) {
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
    
    @Override
    public abstract IDataSignature ofString(final String info);
    
    /**
     * Gets the IDataSignature represented by the string.
     * 
     * @param string - the string representation of the data signature to be created
     * @return the IDataSignature represented by the string 
     * or {@code null} if the string does not represent a valid signature
     * @throws IllegalArgumentException if a number format exception occurs during parsing numeric
     * parts of the given string
     * @see #toString()
     */
    public static IDataSignature of(final String string) throws IllegalArgumentException {
        final String name = string.replaceFirst(REPL_REGEX_NAME_STR, "");
        final String info = string.replaceFirst(REPL_REGEX_INFO_STR, "");
        final int id = getDataIdOfName(name);
        return createDataSignatureByIdAndInfo(id, info);
    }
    
    @Override
    public int hashCode() {
        final IDataSignature[] components = getComponents();
        return Objects.hash(getDataId(), Arrays.deepHashCode(components));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof DataSignature) {
            final DataSignature otherSig = (DataSignature)other;
            final IDataSignature[] components = getComponents();
            final IDataSignature[] otherComponents = otherSig.getComponents();
            return getDataId() == otherSig.getDataId() && Arrays.deepEquals(components, otherComponents);
        }
        return false;
    }

    @Override
    public Iterator<IDataSignature> iterator() {
        return Arrays.asList(getComponents()).iterator();
    }
    
    public static class DontCareDataSignature extends DataSignature {
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
        public IDataSignature getCopy() {
            final DontCareDataSignature ret = new DontCareDataSignature();
            ret.info = "" + this.info;
            return ret;
        }

        @Override
        public IDataSignature getComponent(int index) {
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
        public IDataSignature ofString(final String info) {
            this.info = info;
            return getCopy();
        }
    }
}
