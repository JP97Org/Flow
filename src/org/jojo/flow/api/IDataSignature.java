package org.jojo.flow.api;

import java.io.Serializable;

public interface IDataSignature extends IAPI, Iterable<IDataSignature>, Serializable {
    public static IDataSignature getDefaultImplementation(){
        return (IDataSignature) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    IDataSignature getCopy();
    
    IDataSignature tryGetHashEfficientCopy();
    
    boolean isRecursiveSignature();
    
    boolean isSingleTypeRecursiveSignature();
    
    IDataSignature deactivateChecking();
    
    boolean isChecking();
    
    boolean isCheckingRecursive();
    
    boolean isHashEfficient();
    
    IDataSignature getComponent(final int index);
    
    int size();
    
    IDataSignature[] getComponents();
    
    int getDataId();
    
    Class<?> getDataClass();

    @Override
    String toString();
    
    IDataSignature ofString(final String info);
}
