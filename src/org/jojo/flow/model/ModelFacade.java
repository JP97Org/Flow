package org.jojo.flow.model;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElement;
import org.jojo.flow.model.storeLoad.DynamicClassLoader.MockModule;

public class ModelFacade {
    public static FlowChart flowChart; //TODO
    public static MockModule mock; //TODO remove
    
    public ModelFacade() {
        
    }
    
    public FlowChartElement getElementById(final int id) {
        //TODO
        if (id == 100) {
            return mock;
        }
        return flowChart; //TODO
    }

    public FlowChart getFlowChart() {
        //TODO
        return flowChart; //TODO
    }
}
