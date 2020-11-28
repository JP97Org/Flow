package org.jojo.flow.test.model.flowChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jojo.flow.model.api.IFlowChartElementGR;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.flowChart.FlowChartGR;
import org.jojo.flow.model.flowChart.connections.ConnectionLineGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrowGR;
import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.flowChart.connections.RigidConnectionGR;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.flowChart.modules.RigidPinGR;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModuleGR;
import org.junit.*;

import static org.jojo.flow.model.util.DynamicObjectLoader.loadGR;
import static org.jojo.flow.model.util.DynamicObjectLoader.loadModule;

public class GRTest {
    private IGraphicalRepresentation[] grs;
    
    @Before
    public void setUp() {
        this.grs = getAllGRs();
        System.out.println(Arrays.stream(this.grs)
                .map(g -> g.getClass().getSimpleName())
                .collect(Collectors.toList()));
    }

    private IGraphicalRepresentation[] getAllGRs() {
        final List<IGraphicalRepresentation> grs = new ArrayList<>();
        grs.add(loadGR(FlowChartGR.class.getName()));
        grs.add(loadGR(ConnectionLineGR.class.getName()));
        grs.add(loadGR(DefaultArrowGR.class.getName()));
        grs.add(loadGR(OneConnectionGR.class.getName(), true));
        grs.add(loadGR(OneConnectionGR.class.getName(), false));
        grs.add(loadGR(RigidConnectionGR.class.getName()));
        grs.add(loadGR(DefaultInputPinGR.class.getName()));
        grs.add(loadGR(DefaultOutputPinGR.class.getName()));
        grs.add(loadGR(RigidPinGR.class.getName()));
        grs.add(loadGR(MockModuleGR.class.getName()));
        ((IModuleGR) grs.get(grs.size() - 1)).setModuleMock((MockModule) loadModule(MockModule.class.getName(), 100));
        return grs.toArray(IGraphicalRepresentation[]::new);
    }
    
    @Test
    public void get() {
        for (final var gr : this.grs) {
            Assert.assertTrue(gr.getPosition() != null);
            if (gr instanceof IFlowChartElementGR) {
                ((IFlowChartElementGR) gr).removeLabel();
                Assert.assertTrue(((IFlowChartElementGR) gr).getLabel() == null);
            }
            Assert.assertTrue(gr.isDOMValid(gr.getDOM()));
        }
    }
}
