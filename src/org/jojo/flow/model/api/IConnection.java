package org.jojo.flow.model.api;

import java.util.List;
import java.util.Set;

import org.jojo.flow.exc.ConnectionException;

public interface IConnection extends IFlowChartElement {

    boolean reconnect();

    boolean connect();

    void disconnect();

    String getName();

    void setName(String name);

    String getInfo();

    IOutputPin getFromPin();

    List<IInputPin> getToPins();

    Set<IFlowModule> getConnectedModules();

    boolean isPinImpInConnection(IModulePinImp modulePinImp);

    boolean addToPin(IInputPin toPin) throws ConnectionException;

    boolean removeToPin(IInputPin toPin);

    boolean removeToPin(int index);

    boolean setFromPin(IOutputPin fromPin) throws ConnectionException;
}