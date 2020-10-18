package org.jojo.flow.model.api;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ModuleRunException;
import org.jojo.flow.exc.TimeoutException;

public interface ISimulation extends IAPI {

    void start() throws ModuleRunException, TimeoutException, FlowException;

    void stepOnce() throws ModuleRunException, TimeoutException, FlowException;

    void stop() throws FlowException;

    void forceStop();

    void pause();

    boolean isRunning();

    ISimulationConfiguration getConfig() throws IllegalStateException;

    void setConfig(ISimulationConfiguration config) throws IllegalStateException;

    void reloadStepper();
}