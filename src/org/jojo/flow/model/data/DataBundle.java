package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Iterator;

public class DataBundle extends RecursiveCheckable implements Iterable<Data> {
    /**
     * 
     */
    private static final long serialVersionUID = -1411478059148157427L;
    private final Data[] data;
    private final DataSignature dataSignature;
    
    public DataBundle(final Data[] data) throws DataTypeIncompatException {
        this.data = data;
        final DataSignature[] componentSignatures = Arrays.stream(data)
                .map(x -> x.getDataSignature())
                .toArray(DataSignature[]::new);
        if (Arrays.stream(componentSignatures).allMatch(x -> !x.isCheckingRecursive())) {
            throw new DataTypeIncompatException("all component signatures must be checking recursive");
        }
        this.dataSignature = new RecursiveSignature(this);
    }
    
    @Override
    public Data get(int index) {
        return this.data[index];
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public boolean isSizeConstant() {
        return true;
    }

    @Override
    protected int getDataId() {
        return DataSignature.BUNDLE;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.data);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            final DataBundle otherM = (DataBundle)other;
            return Arrays.equals(this.data, otherM.data);
        }
        return false;
    }

    @Override
    public Iterator<Data> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public Data next() {
                if (hasNext()) {
                    return get(index++);
                }
                return null;
            }
        };
    }

}
