package org.jojo.flow.model.flowChart.modules;

import java.awt.Point;

import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartElementGR;

public class ModuleGR extends FlowChartElementGR {
    private final Module module;
    
    public ModuleGR(final Point position, final Module module, final FlowChart flowChart) {
        super(position, flowChart);
        this.module = module;
    }

    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0; //TODO
    }

    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0; //TODO
    }

}
