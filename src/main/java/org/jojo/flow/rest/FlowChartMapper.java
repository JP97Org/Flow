package org.jojo.flow.rest;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IConnectionGR;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDefaultPin;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowChartGR;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.api.IModulePin;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.rest.dto.ConnectionDto;
import org.jojo.flow.rest.dto.ConnectionRouteDto;
import org.jojo.flow.rest.dto.FlowChartDto;
import org.jojo.flow.rest.dto.FlowChartSummary;
import org.jojo.flow.rest.dto.ModuleDto;
import org.jojo.flow.rest.dto.PinDto;
import org.jojo.flow.rest.dto.PinReferenceDto;
import org.jojo.flow.rest.dto.PointDto;
import org.jojo.flow.rest.dto.WarningDto;

final class FlowChartMapper {
    private FlowChartMapper() {
    }

    static FlowChartSummary toSummary(final IFlowChart flowChart) {
        if (flowChart == null) {
            return null;
        }
        return new FlowChartSummary(
                flowChart.getId(),
                flowChart.getModules().stream().map(IFlowChartElement::getId).toList(),
                flowChart.getConnections().stream().map(IFlowChartElement::getId).toList());
    }

    static FlowChartDto toDto(final IFlowChart flowChart) {
        if (flowChart == null) {
            return null;
        }
        final IGraphicalRepresentation graphicalRepresentation = flowChart.getGraphicalRepresentation();
        final IFlowChartGR flowChartGR = graphicalRepresentation instanceof IFlowChartGR gr ? gr : null;
        final Point absoluteOrigin = flowChartGR == null
                ? graphicalRepresentation.getPosition()
                : flowChartGR.getAbsOriginPoint();
        return new FlowChartDto(
                flowChart.getId(),
                PointDto.from(graphicalRepresentation.getPosition()),
                graphicalRepresentation.getWidth(),
                graphicalRepresentation.getHeight(),
                flowChartGR != null && flowChartGR.isRasterEnabled(),
                PointDto.from(absoluteOrigin),
                flowChart.getModules().stream().map(FlowChartMapper::toDto).toList(),
                flowChart.getConnections().stream().map(FlowChartMapper::toDto).toList(),
                toWarnings(flowChart.getWarnings()));
    }

    static ModuleDto toDto(final IFlowModule module) {
        if (module == null) {
            return null;
        }
        final IGraphicalRepresentation graphicalRepresentation = module.getGraphicalRepresentation();
        final IModuleGR moduleGR = graphicalRepresentation instanceof IModuleGR gr ? gr : null;
        final List<? extends IModulePin> inputPins = module.getAllInputs();
        final List<? extends IModulePin> outputPins = module.getAllOutputs();
        final List<PinDto> inputs = inputPins.stream()
                .map(pin -> toDto(pin, inputPins.indexOf(pin), "input"))
                .toList();
        final List<PinDto> outputs = outputPins.stream()
                .map(pin -> toDto(pin, outputPins.indexOf(pin), "output"))
                .toList();
        return new ModuleDto(
                module.getId(),
                module.getClass().getName(),
                module.getExternalConfig().getName(),
                module.getExternalConfig().getPriority(),
                PointDto.from(graphicalRepresentation.getPosition()),
                graphicalRepresentation.getWidth(),
                graphicalRepresentation.getHeight(),
                moduleGR == null ? 1.0 : moduleGR.getScale(),
                moduleGR == null ? null : moduleGR.getIconText(),
                moduleGR == null ? null : moduleGR.getInfoText(),
                inputs,
                outputs,
                toWarnings(module.getWarnings()));
    }

    static ConnectionDto toDto(final IConnection connection) {
        if (connection == null) {
            return null;
        }
        final String dataSignature = connection instanceof IDefaultArrow arrow
                ? String.valueOf(arrow.getDataSignature())
                : null;
        return new ConnectionDto(
                connection.getId(),
                connection.getClass().getName(),
                connection.getName(),
                connection.getInfo(),
                toReference(connection.getFromPin(), "output"),
                connection.getToPins().stream().map(pin -> toReference(pin, "input")).toList(),
                dataSignature,
                toRoutes(connection),
                toWarnings(connection.getWarnings()));
    }

    private static List<ConnectionRouteDto> toRoutes(final IConnection connection) {
        if (!(connection.getGraphicalRepresentation() instanceof IConnectionGR connectionGR)) {
            return List.of();
        }
        final List<ConnectionRouteDto> routes = new ArrayList<>();
        for (final IOneConnectionGR singleConnection : connectionGR.getSingleConnections()) {
            final PinReferenceDto to = toReference(connection, singleConnection.getToPin());
            if (to == null) {
                continue;
            }
            routes.add(new ConnectionRouteDto(
                    to,
                    singleConnection.getDiversionPoints().stream().map(PointDto::from).toList(),
                    singleConnection.isPathCustomized()));
        }
        return routes;
    }

    private static PinReferenceDto toReference(final IConnection connection,
            final IModulePinGR toPinGR) {
        return connection.getToPins().stream()
                .filter(pin -> pin.getGraphicalRepresentation() == toPinGR)
                .findFirst()
                .map(pin -> toReference(pin, "input"))
                .orElse(null);
    }

    static List<WarningDto> toWarnings(final List<Warning> warnings) {
        return warnings.stream()
                .map(warning -> new WarningDto(
                        warning.getId(),
                        warning.getDescription(),
                        warning.isError(),
                        warning.hasAffectedElement() ? warning.getAffectedElement().getId() : null))
                .toList();
    }

    static WarningDto toDto(final Warning warning) {
        return warning == null
                ? null
                : new WarningDto(
                        warning.getId(),
                        warning.getDescription(),
                        warning.isError(),
                        warning.hasAffectedElement() ? warning.getAffectedElement().getId() : null);
    }

    private static PinDto toDto(final IModulePin pin, final int index, final String direction) {
        final IGraphicalRepresentation graphicalRepresentation = pin.getGraphicalRepresentation();
        final IModulePinGR pinGR = graphicalRepresentation instanceof IModulePinGR gr ? gr : null;
        final String dataSignature = pin.getModulePinImp() instanceof IDefaultPin defaultPin
                ? String.valueOf(defaultPin.getCheckDataSignature())
                : null;
        final IData defaultData = pin.getDefaultData();
        return new PinDto(
                index,
                direction,
                pin.getClass().getName(),
                pin.getModulePinImp().getClass().getName(),
                dataSignature,
                defaultData == null ? null : defaultData.toString(),
                PointDto.from(graphicalRepresentation.getPosition()),
                pinGR == null ? null : PointDto.from(pinGR.getLinePoint()),
                pinGR == null ? null : pinGR.getWidth(),
                pinGR == null ? null : pinGR.getHeight(),
                pinGR == null ? null : pinGR.getIconText(),
                pinGR == null || pinGR.getPinOrientation() == null ? null : pinGR.getPinOrientation().name(),
                pin.getConnections().stream().map(IFlowChartElement::getId).toList());
    }

    private static PinReferenceDto toReference(final IModulePin pin, final String direction) {
        final IFlowModule module = pin.getModule();
        final List<? extends IModulePin> pins = "output".equals(direction)
                ? module.getAllOutputs()
                : module.getAllInputs();
        return new PinReferenceDto(module.getId(), pins.indexOf(pin), direction);
    }
}
