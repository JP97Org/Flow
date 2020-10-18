package org.jojo.flow.model.api;

public interface IRecursiveCheckable extends IData, Iterable<IData> {
    
    IData get(int index);
    
    int size();
    
    boolean isSizeConstant();
}
