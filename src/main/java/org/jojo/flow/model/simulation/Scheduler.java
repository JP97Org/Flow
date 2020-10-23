package org.jojo.flow.model.simulation;

import java.util.List;

import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IScheduler;

public abstract class Scheduler implements IScheduler {
    @Override
    public abstract List<IFlowModule> getSchedule(List<IFlowModule> modulesToStep);
}
