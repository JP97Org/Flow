package org.jojo.flow.model.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jojo.flow.model.flowChart.modules.FlowModule;

public class PriorityScheduler extends Scheduler {
    @Override
    public List<FlowModule> getSchedule(final List<FlowModule> modules) {
        final List<FlowModule> ret = new ArrayList<>(modules);
        Collections.sort(ret); // ascending order of priorities -> lowest priority first
        Collections.reverse(ret); // descending order of priorities -> highest priority first
        return ret;
    }
}
