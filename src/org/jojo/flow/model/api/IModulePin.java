package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.exc.ListSizeException;

/**
 * This interface represents a super-interface for module pins.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModulePin extends IDOMable {

    /**
     * Gets the graphical representation of this pin.
     * 
     * @return the graphical representation of this pin
     */
    IGraphicalRepresentation getGraphicalRepresentation();

    /**
     * Gets the module of this pin using the respective implementation's method.
     * 
     * @return the module of this pin
     * @see IModulePinImp#getModule()
     */
    IFlowModule getModule();

    /**
     * Gets the connections list using the respective implementation's method.
     * 
     * @return the connections list
     * @see IModulePinImp#getConnections()
     */
    List<IConnection> getConnections();

    /**
     * Adds the given connection using the respective implementation's method.
     * 
     * @param toAdd - the given connection
     * @return whether the connection was added
     * @throws ListSizeException if this pin is an IInputPin and more than one connection is added
     * @see IModulePinImp#addConnection(IConnection)
     */
    boolean addConnection(IConnection toAdd) throws ListSizeException;

    /**
     * Removes the given connection using the respective implementation's method.
     * 
     * @param toRemove - the given connection
     * @return whether the connection was removed
     */
    boolean removeConnection(IConnection toRemove);

    /**
     * Gets the pin's default data using the respective implementation's method.
     * 
     * @return the pin's default data
     * @see IModulePinImp#getDefaultData()
     */
    IData getDefaultData();

    /**
     * Gets the module pin implementation.
     * 
     * @return the module pin implementation
     * @see IModulePinImp
     */
    IModulePinImp getModulePinImp();

    /**
     * Gets a hashCode for this instance which remains the same even after program restart.
     * 
     * @return a hashCode for this instance which remains the same even after program restart
     */
    int fixedHashCode();
}