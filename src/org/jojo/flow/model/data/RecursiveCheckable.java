package org.jojo.flow.model.data;

abstract class RecursiveCheckable extends Data {
    /**
     * 
     */
    private static final long serialVersionUID = 7347521500996385348L;
    public abstract Data get(int index);
    public abstract int size();
    public abstract boolean isSizeConstant();
}
