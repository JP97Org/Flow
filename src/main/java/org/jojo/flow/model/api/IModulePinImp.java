package org.jojo.flow.model.api;

import java.util.List;

/**
 * This interface represents a super-interface for implementations of module pins. 
 * As of version 1.0, the two implementations for pins are {@link IDefaultPin} and {@link IRigidPin}.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IModulePinImp extends IAPI {
    
    /**
     * Gets the flow module to which this module pin is attached. It cannot be {@code null}.
     * 
     * @return the flow module to which this module pin is attached
     */
    public IFlowModule getModule();
    
    /**
     * Gets a copy of the connections list of this pin sorted by {@link IFlowChartElement#getId()}.
     * 
     * @return a copy of the connections list of this pin
     */
    List<IConnection> getConnections();
    
    /**
     * Adds the given connection to this pin's connections list. If this method does not succeed, nothing changes.
     * 
     * @param toAdd - the connection to be added
     * @return whether the connection could be added, i.e. this pin is a pin of the given connection
     * @see IConnection#isPinImpInConnection(IModulePinImp)
     */
    boolean addConnection(final IConnection toAdd);
    
    /**
     * Removes the given connection from this pin's connections list.
     * 
     * @param toRemove - the connection to be removed
     * @return whether the connection was removed, i.e. it was an element of the list before
     */
    boolean removeConnection(final IConnection toRemove);
    
    /**
     * Gets the default data of this module pin imp.
     * 
     * @return the default data of this module pin imp 
     * (it may be {@code null} but it should not be {@code null} if possible)
     */
    IData getDefaultData();
}
