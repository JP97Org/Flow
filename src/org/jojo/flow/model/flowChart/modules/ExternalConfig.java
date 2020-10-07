package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.data.Pair;

public class ExternalConfig implements Comparable<ExternalConfig> {
    private final String name;
    private int priority;
    
    public ExternalConfig(final String name, final int priority) {
        this.name = name;
        this.priority = priority;
    }
    
    public void setPriority(final int newPriority) {
        this.priority = newPriority;
    }
    
    public Pair<String, Integer> getConfig() {
        return new Pair<>(this.name, this.priority);
    }
    
    @Override
    public int hashCode() {
        return getConfig().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other != null && other instanceof ExternalConfig) {
            final ExternalConfig oc = (ExternalConfig)other;
            return this.name == oc.name && this.priority == oc.priority;
        }
        return false;
    }

    @Override
    public int compareTo(final ExternalConfig o) {
        if (this.priority != o.priority) {
            return Integer.valueOf(this.priority).compareTo(o.priority);
        }
        return this.name.compareTo(o.name);
    }
}
