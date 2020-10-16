package org.jojo.flow.model.data;

import java.util.Iterator;

import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IRecursiveCheckable;

abstract class RecursiveCheckable extends Data implements IRecursiveCheckable {
    /**
     * 
     */
    private static final long serialVersionUID = 7347521500996385348L;
    
    @Override
    public abstract IData get(int index);
    
    @Override
    public abstract int size();
    
    @Override
    public abstract boolean isSizeConstant();
    
    @Override
    public Iterator<IData> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @Override
            public IData next() {
                if (hasNext()) {
                    return get(index++);
                }
                return null;
            }
        };
    }
}
