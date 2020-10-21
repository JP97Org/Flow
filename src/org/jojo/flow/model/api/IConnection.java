package org.jojo.flow.model.api;

import java.util.List;
import java.util.Set;

import org.jojo.flow.exc.ConnectionException;

/**
 * This interface represents a super-interface to all connections on the flow chart.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface IConnection extends IFlowChartElement {
    
    /**
     * Determines whether the two pins are connectable with a connection of the correct type.
     * 
     * @param from - the from pin
     * @param to - the to pin
     * @return whether the two pins are connectable
     */
    public static boolean areConnectable(final IOutputPin from, final IInputPin to) {
        if (from.getModulePinImp() instanceof IRigidPin) {
            return to.getModulePinImp() instanceof IRigidPin;
        } else if (from.getModulePinImp() instanceof IDefaultPin) {
            if (to.getModulePinImp() instanceof IDefaultPin) {
                final var otherSig = ((IDefaultPin)to.getModulePinImp()).getCheckDataSignature();
                return ((IDefaultPin)from.getModulePinImp()).getCheckDataSignature().matches(otherSig);
            }
            return false;
        }
        return false; //unknown connection type
    }
    
    /**
     * Reconnects this connection, i.e. disconnects it and connects it afterwards.
     * Note that reconnecting must always be successful if the pins of this connection did not change.
     * This method leaves the connection unconnected if it does not succeed connecting.
     * 
     * @return whether the connecting was successful
     * @see #disconnect()
     * @see #connect()
     */
    boolean reconnect();

    /**
     * Connects this connection. Note that the connection must be unconnected in order for this method
     * to function properly. If this connection is already connected or if it is not sure whether it is
     * connected, use {@link #reconnect()} instead. If the connection cannot be established, a Warning may
     * be reported to this connection.
     * 
     * @return whether the connecting was successful
     */
    boolean connect();

    /**
     * Disconnects this connection, i.e. removes this connection from all its pin's connections lists.
     * If this connection is already disconnected, nothing changes.
     */
    void disconnect();

    /**
     * Gets the name of this connection which must not be {@code null}.
     * 
     * @return the name of this connection
     */
    String getName();

    /**
     * Sets the name of this connection to the given name which must not be {@code null}.
     * @param name - the given name
     */
    void setName(String name);

    /**
     * Gets an info text (must not be {@code null}) about this connection.
     * 
     * @return an info text about this connection
     */
    String getInfo();

    /**
     * Gets the from pin of this connection which must not be {@code null}.
     * 
     * @return the from pin of this connection
     */
    IOutputPin getFromPin();

    /**
     * Gets a copy of the to pins list of this connection.
     * 
     * @return a copy of the to pins list of this connection
     */
    List<IInputPin> getToPins();

    /**
     * Gets a set of IFlowModule instances to which this connection is connected.
     * 
     * @return a set of IFlowModule instances to which this connection is connected
     */
    Set<IFlowModule> getConnectedModules();

    /**
     * Determines whether the given IModulePinImp is inside this connection, i.e. either the from or a to pin.
     * 
     * @param modulePinImp - the given IModulePinImp
     * @return whether the given IModulePinImp is inside this connection
     */
    boolean isPinImpInConnection(IModulePinImp modulePinImp);

    /**
     * Adds the given IInputPin as a new to pin to this connection. 
     * If this method is unsuccessful, nothing changes.
     * 
     * @param toPin - the given to pin
     * @return whether the adding succeeded, i.e the Impl types match and the datatypes match
     * @throws  ConnectionException if the toPin to be added does not have the same Impl type 
     *          as this connection's from pin
     */
    boolean addToPin(IInputPin toPin) throws ConnectionException;

    /**
     * Removes the given to pin.
     * 
     * @param toPin - the given to pin
     * @return whether the pin was removed
     */
    boolean removeToPin(IInputPin toPin);

    /**
     * Sets the given pin as the new from pin.
     * If this method is unsuccessful, nothing changes.
     * 
     * @param fromPin - the given pin
     * @return whether setting the pin succeeded, i.e. the Impl types match and the datatypes match
     * @throws ConnectionException if the fromPin to be set does not have the same Impl type 
     *          as this connection's to pins
     */
    boolean setFromPin(IOutputPin fromPin) throws ConnectionException;
}