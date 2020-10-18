package org.jojo.flow.model.api;

public interface IExternalConfig extends IAPI, Comparable<IExternalConfig> {

    void setModule(IFlowModule module);

    String getName();

    int getPriority();

    void setPriority(int newPriority);

    Pair<String, Integer> getConfig();
}