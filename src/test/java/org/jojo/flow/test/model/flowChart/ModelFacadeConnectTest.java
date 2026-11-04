package org.jojo.flow.test.model.flowChart;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelFacadeConnectTest {

    @Test
    public void connectsTwoInstancesOfTheSameModuleTypeWithUniqueIds() {
        final ModelFacade facade = new ModelFacade(true);
        final FlowChart flowChart = new FlowChart(500, new FlowChartGR());
        final MockModule first = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 501);
        final MockModule second = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 502);
        flowChart.addModule(first);
        flowChart.addModule(second);

        Assertions.assertTrue(facade.connect(flowChart,
                first.getDefaultOutputs().get(0), second.getDefaultInputs().get(0)));
        Assertions.assertTrue(facade.connect(flowChart,
                second.getDefaultOutputs().get(0), first.getDefaultInputs().get(0)));

        Assertions.assertEquals(2, flowChart.getConnections().size());
        Assertions.assertEquals(2, flowChart.getConnections().stream()
                .map(connection -> connection.getId())
                .distinct()
                .count());
    }

    @Test
    public void removingModuleFreesItsPinsAndRemovesItsConnections() {
        final ModelFacade facade = new ModelFacade(true);
        final FlowChart flowChart = new FlowChart(600, new FlowChartGR());
        final MockModule first = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 601);
        final MockModule middle = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 602);
        final MockModule last = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 603);
        flowChart.addModule(first);
        flowChart.addModule(middle);
        flowChart.addModule(last);

        Assertions.assertTrue(facade.connect(flowChart,
                first.getDefaultOutputs().get(0), middle.getDefaultInputs().get(0)));
        Assertions.assertTrue(facade.connect(flowChart,
                middle.getDefaultOutputs().get(0), last.getDefaultInputs().get(0)));
        Assertions.assertEquals(2, flowChart.getConnections().size());

        Assertions.assertTrue(facade.removeModule(flowChart, middle.getId()));

        Assertions.assertTrue(flowChart.getConnections().isEmpty());
        Assertions.assertTrue(first.getDefaultOutputs().get(0).getConnections().isEmpty());
        Assertions.assertTrue(last.getDefaultInputs().get(0).getConnections().isEmpty());
        Assertions.assertTrue(facade.connect(flowChart,
                first.getDefaultOutputs().get(0), last.getDefaultInputs().get(0)));
    }
}
