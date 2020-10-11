package org.jojo.flow.model;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.storeLoad.DynamicObjectLoader.MockModule;

public class ModelFacade {
    public static FlowChart flowChart; //TODO
    public static MockModule mock; //TODO remove
    
    public ModelFacade() {
        
    }
    
    public FlowChartElement getElementById(final int id) {
        //TODO
        if (getFlowChart().getConnections().stream().anyMatch(x -> x.getId() == id)) {
            return getFlowChart().getConnections()
                    .stream()
                    .filter(x -> x.getId() == id)
                    .findFirst().orElse(null);
        } else if (getFlowChart().getModules().stream().anyMatch(x -> x.getId() == id)) {
            return getFlowChart().getModules()
                    .stream()
                    .filter(x -> x.getId() == id)
                    .findFirst().orElse(null);
        }
        return flowChart; //TODO
    }

    public FlowChart getFlowChart() {
        //TODO
        return flowChart; //TODO
    }
}
