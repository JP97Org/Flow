package org.jojo.flow.rest;

import java.awt.Point;
import java.util.List;

import org.jojo.flow.model.api.IConnectionGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.model.util.DynamicObjectLoader.MockModule;
import org.jojo.flow.rest.dto.ConnectionDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowChartMapperRouteTest {

    @Test
    public void mapsCustomizedConnectionRoutes() throws Exception {
        final MockModule mock = (MockModule) DynamicObjectLoader.loadModule(
                DynamicObjectLoader.MockModule.class.getName(), 710);
        final DefaultArrow arrow = new DefaultArrow(711,
                mock.getDefaultOutputs().get(0), mock.getDefaultInputs().get(0), "Arrow");
        final IOneConnectionGR single = ((IConnectionGR) arrow.getGraphicalRepresentation())
                .getSingleConnections().get(0);
        single.setCustomPath(List.of(new Point(4, 3), new Point(6, 3)));

        final ConnectionDto dto = FlowChartMapper.toDto(arrow);

        Assertions.assertEquals(1, dto.routes().size());
        Assertions.assertTrue(dto.routes().get(0).customized());
        Assertions.assertEquals(2, dto.routes().get(0).diversionPoints().size());
        Assertions.assertEquals(new Point(4, 3),
                dto.routes().get(0).diversionPoints().get(0).toPoint());
    }
}
