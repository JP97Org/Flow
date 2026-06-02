package org.jojo.flow.rest;

import java.awt.Point;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IModelFacade;
import org.jojo.flow.model.api.IOutputPin;

@ApplicationScoped
public class ModelFacadeService {
    private final IModelFacade facade = IModelFacade.getDefaultImplementation();
    private final IDynamicClassLoader dynamicClassLoader = IDynamicClassLoader.DEFAULT_DYNAMIC_LOADER;

    @PostConstruct
    void init() {
        if (facade.getMainFlowChart() == null || facade.getMainFlowChart().getId() == -1) {
            facade.setMainFlowChart(IFlowChart.getDefaultImplementation());
        }
    }

    public FlowChartSummary getMainFlowChartSummary() {
        return toSummary(facade.getMainFlowChart());
    }

    public FlowChartSummary getFlowChartSummary(final int id) {
        return toSummary(facade.getFlowChartById(id));
    }

    public int nextFreeId() {
        return facade.nextFreeId();
    }

    public FlowChartSummary createNewMainFlowChart() {
        final IFlowChart flowChart = IFlowChart.getDefaultImplementation();
        facade.setMainFlowChart(flowChart);
        return toSummary(flowChart);
    }

    public boolean addModule(final AddModuleRequest request) {
        Objects.requireNonNull(request);
        final Point position = request.position().toPoint();
        return facade.addModule(dynamicClassLoader, request.className(), position);
    }

    public boolean removeModule(final int id) {
        return facade.removeModule(id);
    }

    public boolean removeConnection(final int id) {
        return facade.removeConnection(id);
    }

    public boolean connect(final ConnectRequest request) {
        Objects.requireNonNull(request);
        final IFlowChart chart = facade.getMainFlowChart();
        if (chart == null) {
            return false;
        }

        final IFlowModule fromModule = findModule(chart, request.fromModuleId());
        final IFlowModule toModule = findModule(chart, request.toModuleId());
        if (fromModule == null || toModule == null) {
            return false;
        }

        final List<IOutputPin> outputs = fromModule.getAllOutputs();
        final List<IInputPin> inputs = toModule.getAllInputs();

        if (request.fromOutputIndex() < 0 || request.fromOutputIndex() >= outputs.size()) {
            return false;
        }
        if (request.toInputIndex() < 0 || request.toInputIndex() >= inputs.size()) {
            return false;
        }

        return facade.connect(chart, outputs.get(request.fromOutputIndex()), inputs.get(request.toInputIndex()));
    }

    public ValidationResult validateMainFlowChart() {
        return validateFlowChart(facade.getMainFlowChart());
    }

    public ValidationResult validateFlowChart(final int flowChartId) {
        return validateFlowChart(facade.getFlowChartById(flowChartId));
    }

    private ValidationResult validateFlowChart(final IFlowChart flowChart) {
        if (flowChart == null) {
            return new ValidationResult(false, null, "Flow chart not found");
        }
        try {
            final IDefaultArrow invalidArrow = flowChart.validate();
            if (invalidArrow == null) {
                return new ValidationResult(true, null, null);
            }
            return new ValidationResult(false, invalidArrow.getId(), null);
        } catch (ValidationException e) {
            return new ValidationResult(false, null, e.getMessage());
        }
    }

    private FlowChartSummary toSummary(final IFlowChart flowChart) {
        if (flowChart == null) {
            return null;
        }
        return new FlowChartSummary(
                flowChart.getId(),
                flowChart.getModules().stream().map(IFlowChartElement::getId).toList(),
                flowChart.getConnections().stream().map(IFlowChartElement::getId).toList());
    }

    private IFlowModule findModule(final IFlowChart flowChart, final int moduleId) {
        return flowChart.getModules().stream().filter(m -> m.getId() == moduleId).findFirst().orElse(null);
    }
}
