package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

/**
 * This interface represents a default arrow, i.e. a connection used for sending data from an
 * IOutputPin to one ore more IInputPin.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IDefaultArrow extends IConnection {
    
    /**
     * Gets the default implementation.
     * 
     * @param from - the from pin of this connection (must not be {@code null})
     * @param to - the to pin of this connection (must not be {@code null})
     * @param name - the name of the connection (must not be {@code null})
     * @return the default implementation
     */
    public static IDefaultArrow getDefaultImplementation(final IOutputPin from, final IInputPin to, final String name) {
        return (IDefaultArrow) DynamicObjectLoader.loadConnection(
                IModelFacade.getDefaultImplementation().nextFreeId(), from, to, name);
    }

    /**
     * Gets the data on this arrow or {@code null} if no data has been put.
     * 
     * @return the data on this arrow or {@code null} if no data has been put
     */
    IData getData();

    /**
     * Puts data on the arrow if the IDataSignature of the given IData matches this arrow's IDataSignature.
     * If this method does not succeed, nothing changes.
     *
     * @param data - the data to be put on the arrow or {@code null} if the data should be removed from the arrow
     * @return whether the putting succeeded
     * @see IData#hasSameType(IDataSignature)
     */
    boolean putData(IData data);

    /**
     * Gets the data signature of this arrow. After creation of an instance the data signature is the one
     * of the from pin.
     * 
     * @return the data signature of this arrow
     */
    IDataSignature getDataSignature();

    /**
     * Sets a data signature to the arrow which matches the data signature at the moment,
     * but must be completely checking. If this method does not succeed nothing changes.
     * 
     * @param iDataSignature - the given data signature which must be recursively checking
     * @return whether putting the new data signature was successful
     * @see #forcePutDataSignature(IDataSignature)
     */
    boolean putDataSignature(IDataSignature iDataSignature);

    /**
     * Sets the given data signature without checking if it matches the one at the moment.
     * 
     * @param checkingDataSignature - the given data signature (must not be {@code null})
     * @see #putDataSignature(IDataSignature)
     */
    void forcePutDataSignature(IDataSignature checkingDataSignature);
}