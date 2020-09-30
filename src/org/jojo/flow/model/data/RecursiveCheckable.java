package org.jojo.flow.model.data;

abstract class RecursiveCheckable extends Data {
    public abstract Data get(int index);
    public abstract int size();
    public abstract boolean isSizeConstant();
}
