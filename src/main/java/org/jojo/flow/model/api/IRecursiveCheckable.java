package org.jojo.flow.model.api;

/**
 * This interface represents a super-interface for recursive checkable {@link IData}, i.e. data which is
 * recursive in its nature, i.e. it usually contains other {@link IData} as components.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IRecursiveCheckable extends IData, Iterable<IData> {
    
    /**
     * Gets the data at the given index. If the index is out of bounds the behavior is undefined.
     * 
     * @param index - the given index
     * @return the {@link IData} at the given index
     */
    IData get(int index);
    
    /**
     * Gets the size of this recursive checkable data, i.e. max{indices} + 1.
     * 
     * @return the size of this recursive checkable data, i.e. max{indices} + 1
     */
    int size();
    
    /**
     * Determines whether the size is constant for the lifetime of this instance.
     * 
     * @return whether the size is constant for the lifetime of this instance
     */
    boolean isSizeConstant();
}
