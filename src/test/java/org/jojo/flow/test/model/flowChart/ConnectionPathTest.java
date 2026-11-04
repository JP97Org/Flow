package org.jojo.flow.test.model.flowChart;

import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.api.IConnectionGR;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.flowChart.connections.OneConnectionGR;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConnectionPathTest {

    @Test
    public void customPathIsMarkedAndSurvivesDomRoundTrip() throws Exception {
        final MockModule mock = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 700);
        final DefaultArrow arrow = new DefaultArrow(701,
                mock.getDefaultOutputs().get(0), mock.getDefaultInputs().get(0), "Arrow");
        final IOneConnectionGR single = ((IConnectionGR) arrow.getGraphicalRepresentation())
                .getSingleConnections().get(0);
        Assertions.assertFalse(single.isPathCustomized());

        final List<Point> customPath = List.of(new Point(4, 3), new Point(6, 3));
        Assertions.assertTrue(single.setCustomPath(customPath));
        Assertions.assertTrue(single.isPathCustomized());
        Assertions.assertEquals(customPath, single.getDiversionPoints());

        final IDOM dom = single.getDOM();
        Assertions.assertTrue(single.isDOMValid(dom));
        final OneConnectionGR restored = (OneConnectionGR) DynamicObjectLoader
                .loadGR(OneConnectionGR.class.getName(), true);
        restored.restoreFromDOM(dom);

        Assertions.assertTrue(restored.isPathCustomized());
        Assertions.assertEquals(customPath, restored.getDiversionPoints());
    }
}
