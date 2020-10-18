package org.jojo.flow.model.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jojo.flow.model.api.IFlowModule;

public class PriorityScheduler extends Scheduler {
    @Override
    public List<IFlowModule> getSchedule(final List<IFlowModule> modules) {
        final List<IFlowModule> ret = new ArrayList<>(modules);
        Collections.sort(ret); // ascending order of priorities -> lowest priority first
        Collections.reverse(ret); // descending order of priorities -> highest priority first
        return ret;
    }
}
