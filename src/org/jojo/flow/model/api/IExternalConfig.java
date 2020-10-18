package org.jojo.flow.model.api;

import org.jojo.flow.model.util.DynamicObjectLoader;

public interface IExternalConfig extends ISubject, IDOMable, Comparable<IExternalConfig> {
    public static IExternalConfig getDefaultImplementation() {
        return DynamicObjectLoader.getNewMockExternalConfig();
    }
    
    public static IExternalConfig getDefaultImplementation(final String name, final int priority) {
        return (IExternalConfig) IAPI.defaultImplementationOfThisApi(new Class<?>[] {String.class, int.class}, name, priority);
    }

    void setModule(IFlowModule module);

    String getName();

    int getPriority();

    void setPriority(int newPriority);

    Pair<String, Integer> getConfig();
}