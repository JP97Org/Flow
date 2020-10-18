package org.jojo.flow.model.api;

import java.util.List;

public interface IScheduler extends IAPI {
    public static IScheduler getDefaultImplementation() {
        return (IScheduler) IAPI.defaultImplementationOfThisApi(new Class<?>[] {});
    }

    List<IFlowModule> getSchedule(List<IFlowModule> modulesToStep);
}