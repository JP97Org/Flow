package org.jojo.flow.model.api;

import java.util.List;
import java.util.Set;

import org.jojo.flow.exc.ConnectionException;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.InputPin;
import org.jojo.flow.model.flowChart.modules.ModulePinImp;
import org.jojo.flow.model.flowChart.modules.OutputPin;

public interface IConnection extends IFlowChartElement {

    boolean reconnect();

    boolean connect();

    void disconnect();

    String getName();

    void setName(String name);

    String getInfo();

    OutputPin getFromPin();

    List<InputPin> getToPins();

    Set<FlowModule> getConnectedModules();

    boolean isPinImpInConnection(ModulePinImp modulePinImp);

    boolean addToPin(InputPin toPin) throws ConnectionException;

    boolean removeToPin(InputPin toPin);

    boolean removeToPin(int index);

    boolean setFromPin(OutputPin fromPin) throws ConnectionException;
}