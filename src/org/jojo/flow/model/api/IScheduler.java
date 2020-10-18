package org.jojo.flow.model.api;

import java.util.List;

public interface IScheduler extends IAPI {

    List<IFlowModule> getSchedule(List<IFlowModule> modulesToStep);
}