package org.jojo.flow.model.api;

import org.jojo.flow.model.data.Pair;
import org.jojo.flow.model.flowChart.modules.FlowModule;

public interface IExternalConfig extends IAPI, Comparable<IExternalConfig> {

    void setModule(FlowModule module);

    String getName();

    int getPriority();

    void setPriority(int newPriority);

    Pair<String, Integer> getConfig();
}