package org.jojo.flow.model.api;

import java.io.Serializable;

/**
 * This interface represents a data signature for an IData instance. 
 * However, instances of IDataSignature may also be used as stand-alone objects. When used as such,
 * the checking can be deactivated in order to allow more data signatures to match. 
 * However, IDataSignature instances attached to IData instances should not be altered.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 * @see IData#getDataSignature()
 */
public interface IDataSignature extends IAPI, Iterable<IDataSignature>, Serializable {
    
    /**
     * Gets the default implementation, i.e. a non-checking data signature.
     * Note that this is not the usual way to get a IDataSignature. It should rather be retrieved
     * from an IData instance by {@link IData#getDataSignature()} and {@link #getCopy()}.
     * 
     * @return the default implementation, i.e. a non-checking data signature
     */
    public static IDataSignature getDefaultImplementation(){
        return (IDataSignature) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }
    
    /**
     * Determines whether this data signature match with the given other signature.
     * The relation represented by this method is reflexive and symmetrical but not necessarily
     * transitive because checking can be deactivated.
     * Two data signatures match iff they are equal when the deactivated parts of the type
     * trees of the signatures are ignored.
     * 
     * @param other - the given other data signature
     * @return whether this data signature match with the given other signature
     */
    boolean matches(IDataSignature other);
    
    /**
     * Gets a copy of this IDataSignature, i.e. a data signature which is exactly equal to this
     * one except for object identities of itself and all its components.
     * 
     * @return a copy of this data signature
     */
    IDataSignature getCopy();
    
    /**
     * Determines whether this signature is one for an IRecursiveCheckable instance.
     * 
     * @return whether this signature is one for an IRecursiveCheckable instance.
     */
    boolean isRecursiveSignature();
    
    /**
     * Determines whether this signature is one for an IRecursiveCheckable instance with a single data type,
     * i.e. if all IData instances are of the same type.
     * 
     * @return whether this signature is one for an IRecursiveCheckable instance with a single data type.
     */
    boolean isSingleTypeRecursiveSignature();
    
    /**
     * Deactivates the checking of this data signature. If this signature has components, they are also
     * checked never again. <br/>
     * Note that checking cannot be reactivated once it is deactivated, so consider copying this data signature
     * before deactivating checking. <br/>
     * Also note that it is strongly not recommended to deactivate checking 
     * of a data signature attached to an IData instance.
     * 
     * @return this data signature
     * @see #getCopy()
     * @see #isChecking()
     * @see #isCheckingRecursive()
     */
    IDataSignature deactivateChecking();
    
    /**
     * Determines whether this data signature is checking, i.e. whether {@link #deactivateChecking()}
     * on this instance has been returned at least once. 
     * 
     * @return whether this data signature is checking
     * @see #isCheckingRecursive()
     */
    boolean isChecking();
    
    /**
     * Determines whether this data signature is checking recursively, 
     * i.e. whether this signature is checking and all contained signatures are checking recursively.
     * 
     * @return whether this data signature is checking recursively
     * @see #isChecking()
     */
    boolean isCheckingRecursive();
    
    /**
     * Gets the IDataSignature at the given index. If the index is out of bounds the behavior is undefined.
     * 
     * @param index - the given index
     * @return the component at the given index
     */
    IDataSignature getComponent(final int index);
    
    /**
     * Gets the size of this data signature, i.e. max{indices} + 1.
     * 
     * @return the size of this data signature, i.e. max{indices} + 1
     */
    int size();
    
    /**
     * Gets an array containing all components of this signature.
     * 
     * @return an array containing all components of this signature
     */
    IDataSignature[] getComponents();
    
    /**
     * Gets the data ID of this data type or DONT_CARE (-1) if checking has been deactivated.
     * The data ID should be unique program-widely.
     * 
     * @return the data ID of this data type
     */
    int getDataId();
    
    /**
     * Gets the respective data class represented by this signature.
     * 
     * @return the respective data class represented by this signature or {@code null} 
     * if the data ID is unknown or checking has been deactivated
     */
    Class<?> getDataClass();

    /**
     * Returns a string representation of the object which can be used to recreate a copy of this
     * data signature using e.g. the method {@link org.jojo.flow.model.data.DataSignature#of(String)}.
     */
    String toString();
    
    /**
     * Gets the IDataSignature represented by the data ID of this data signature
     * and the given info string.
     * 
     * @param info - the info part of the {@link #toString()} result, i.e. the part which has nothing 
     * to do with the data ID but rather describes the creation of the respective signature
     * @return the IDataSignature represented by the data ID of this data signature
     * and the given info string
     */
    IDataSignature ofString(final String info);
}
