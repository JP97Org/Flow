package org.jojo.flow.model.api;

import java.util.List;

import org.jojo.flow.model.flowChart.modules.FlowModule;

public interface IScheduler extends IAPI {

    List<FlowModule> getSchedule(List<FlowModule> modules);
}