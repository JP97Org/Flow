package org.jojo.flow.test.model.storeLoad;

import java.io.File;
import java.nio.file.Files;

import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.flowChart.FlowChart;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.modules.FlowModule;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.storeLoad.DocumentString;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowChartRestoreTest {

    @Test
    public void firstLoadWithoutMatchingMainChartKeepsModuleLinks() {
        final FlowModule sourceModule = (FlowModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 7);
        final IFlowChart source = new FlowChart(0, new FlowChartGR());
        source.addModule(sourceModule);
        final IDOM dom = source.getDOM();

        new ModelFacade().setMainFlowChart(new FlowChart(0, new FlowChartGR()));
        final IFlowChart loaded = DynamicObjectLoader.loadFlowChartFromDOM(dom);

        Assertions.assertNotNull(loaded);
        final FlowModule loadedModule = (FlowModule) loaded.getModules().get(0);
        Assertions.assertSame(loadedModule,
                ((ModuleGR) loadedModule.getGraphicalRepresentation()).getModule());
        Assertions.assertDoesNotThrow(() -> loaded.getGraphicalRepresentation().getWidth());
        Assertions.assertDoesNotThrow(() -> loaded.getGraphicalRepresentation().getHeight());
    }

    @Test
    public void connectionEndpointsAreLinkedToRestoredModules() throws Exception {
        final MockModule fromModule = (MockModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 1);
        final MockModule toModule = (MockModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 2);
        final IFlowChart source = new FlowChart(0, new FlowChartGR());
        source.addModule(fromModule);
        source.addModule(toModule);
        final DefaultArrow arrow = new DefaultArrow(3, fromModule.getDefaultOutputs().get(0),
                toModule.getDefaultInputs().get(0), "Arrow");
        Assertions.assertTrue(source.addConnection(arrow));
        final IDOM dom = source.getDOM();

        new ModelFacade().setMainFlowChart(new FlowChart(0, new FlowChartGR()));
        final IFlowChart loaded = DynamicObjectLoader.loadFlowChartFromDOM(dom);

        Assertions.assertNotNull(loaded);
        Assertions.assertEquals(1, loaded.getConnections().size());
        final IConnection connection = loaded.getConnections().get(0);
        Assertions.assertEquals(1, connection.getFromPin().getModule().getId());
        Assertions.assertEquals(2, connection.getToPins().get(0).getModule().getId());
        Assertions.assertSame(
                loaded.getModules().stream().filter(module -> module.getId() == 1)
                        .findFirst().orElseThrow().getAllOutputs().get(0),
                connection.getFromPin());
        Assertions.assertSame(
                loaded.getModules().stream().filter(module -> module.getId() == 2)
                        .findFirst().orElseThrow().getAllInputs().get(0),
                connection.getToPins().get(0));
        Assertions.assertEquals(1, connection.getFromPin().getConnections().size());
        Assertions.assertEquals(1, connection.getToPins().get(0).getConnections().size());

        new ModelFacade().setMainFlowChart(loaded);
        final IFlowChart loadedAgain = DynamicObjectLoader.loadFlowChartFromDOM(source.getDOM());

        Assertions.assertNotNull(loadedAgain);
        final IConnection connectionAgain = loadedAgain.getConnections().get(0);
        Assertions.assertEquals(1, connectionAgain.getFromPin().getModule().getId());
        Assertions.assertEquals(2, connectionAgain.getToPins().get(0).getModule().getId());
        Assertions.assertEquals(1, connectionAgain.getFromPin().getConnections().size());
        Assertions.assertEquals(1, connectionAgain.getToPins().get(0).getConnections().size());
    }

    @Test
    public void xmlRoundTripKeepsConnectionBetweenModules() throws Exception {
        final MockModule fromModule = (MockModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 1);
        final MockModule toModule = (MockModule) DynamicObjectLoader.loadModule(
                MockModule.class.getName(), 2);
        final IFlowChart source = new FlowChart(0, new FlowChartGR());
        source.addModule(fromModule);
        source.addModule(toModule);
        final DefaultArrow arrow = new DefaultArrow(3, fromModule.getDefaultOutputs().get(0),
                toModule.getDefaultInputs().get(0), "Arrow");
        Assertions.assertTrue(source.addConnection(arrow));
        final String xml = new DocumentString(new FlowDOM(source.getDOM()).getDocument()).toString();
        final File file = Files.createTempFile("flow-restore-", ".xml").toFile();
        try {
            Files.writeString(file.toPath(), xml);
            new ModelFacade().setMainFlowChart(new FlowChart(0, new FlowChartGR()));
            final IFlowChart loaded = new StoreLoadFacade().loadFlowChart(file);

            Assertions.assertNotNull(loaded);
            final IConnection connection = loaded.getConnections().get(0);
            Assertions.assertEquals(1, connection.getFromPin().getModule().getId());
            Assertions.assertEquals(2, connection.getToPins().get(0).getModule().getId());
        } finally {
            file.delete();
        }
    }
}
