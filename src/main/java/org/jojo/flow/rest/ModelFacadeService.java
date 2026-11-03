package org.jojo.flow.rest;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.ValidationException;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.api.IDynamicClassLoader;
import org.jojo.flow.model.api.IFlowChart;
import org.jojo.flow.model.api.IFlowChartElement;
import org.jojo.flow.model.api.IFlowModule;
import org.jojo.flow.model.api.IInputPin;
import org.jojo.flow.model.api.IModelFacade;
import org.jojo.flow.model.api.IModuleClassesList;
import org.jojo.flow.model.api.IModuleGR;
import org.jojo.flow.model.api.IOutputPin;
import org.jojo.flow.model.api.ISimulation;
import org.jojo.flow.model.api.ISimulationConfiguration;
import org.jojo.flow.model.api.IStepper;
import org.jojo.flow.model.api.IStoreLoadFacade;
import org.jojo.flow.model.data.Fraction;
import org.jojo.flow.model.data.units.Frequency;
import org.jojo.flow.model.data.units.Time;
import org.jojo.flow.model.storeLoad.DocumentString;
import org.jojo.flow.model.storeLoad.FlowDOM;
import org.jojo.flow.model.storeLoad.ModuleClassesList;
import org.jojo.flow.model.storeLoad.StoreLoadFacade;
import org.jojo.flow.model.simulation.SimulationConfiguration;
import org.jojo.flow.model.util.DynamicObjectLoader;
import org.jojo.flow.rest.dto.AddModuleRequest;
import org.jojo.flow.rest.dto.ConnectRequest;
import org.jojo.flow.rest.dto.ConnectionDto;
import org.jojo.flow.rest.dto.ErrorResponse;
import org.jojo.flow.rest.dto.FlowChartDto;
import org.jojo.flow.rest.dto.FlowChartSummary;
import org.jojo.flow.rest.dto.ModuleClassDto;
import org.jojo.flow.rest.dto.ModuleDto;
import org.jojo.flow.rest.dto.SimulationConfigDto;
import org.jojo.flow.rest.dto.SimulationStatusDto;
import org.jojo.flow.rest.dto.StepForwardRequest;
import org.jojo.flow.rest.dto.StepRequest;
import org.jojo.flow.rest.dto.UpdateConnectionRequest;
import org.jojo.flow.rest.dto.UpdateModuleRequest;
import org.jojo.flow.rest.dto.ValidationResult;

@ApplicationScoped
public class ModelFacadeService {
    private static final Logger LOG = Logger.getLogger(ModelFacadeService.class);

    private final IModelFacade facade = IModelFacade.getDefaultImplementation();
    private final IDynamicClassLoader dynamicClassLoader = IDynamicClassLoader.DEFAULT_DYNAMIC_LOADER;
    private final Map<Integer, SimulationSession> simulations = new LinkedHashMap<>();
    private final Map<String, ModuleClassDto> moduleClasses = new LinkedHashMap<>();

    private IModuleClassesList moduleClassesList;

    @PostConstruct
    void init() {
        if (facade.getMainFlowChart() == null || facade.getMainFlowChart().getId() == -1) {
            facade.setMainFlowChart(IFlowChart.getDefaultImplementation());
        }
        loadModuleCatalog();
    }

    public FlowChartDto getMainFlowChart() {
        return FlowChartMapper.toDto(facade.getMainFlowChart());
    }

    public FlowChartDto getFlowChart(final int id) {
        return FlowChartMapper.toDto(requireFlowChart(id));
    }

    public List<FlowChartSummary> getFlowCharts() {
        final List<FlowChartSummary> result = new ArrayList<>();
        result.add(FlowChartMapper.toSummary(facade.getMainFlowChart()));
        facade.getOtherFlowCharts().stream().map(FlowChartMapper::toSummary).forEach(result::add);
        return result;
    }

    public FlowChartSummary getMainFlowChartSummary() {
        return FlowChartMapper.toSummary(facade.getMainFlowChart());
    }

    public FlowChartSummary getFlowChartSummary(final int id) {
        return FlowChartMapper.toSummary(requireFlowChart(id));
    }

    public int nextFreeId() {
        return facade.nextFreeId();
    }

    public FlowChartDto createNewMainFlowChart() {
        final IFlowChart flowChart = IFlowChart.getDefaultImplementation();
        facade.setMainFlowChart(flowChart);
        simulations.clear();
        return FlowChartMapper.toDto(flowChart);
    }

    public FlowChartDto createFlowChart() {
        final IFlowChart flowChart = IFlowChart.getDefaultImplementation();
        facade.addFlowChart(flowChart);
        return FlowChartMapper.toDto(flowChart);
    }

    public void removeFlowChart(final int id) {
        final IFlowChart flowChart = requireFlowChart(id);
        if (facade.getMainFlowChart().getId() == id) {
            throw apiException(Response.Status.BAD_REQUEST, "The main flow chart cannot be deleted");
        }
        if (!facade.getOtherFlowCharts().remove(flowChart)) {
            throw apiException(Response.Status.NOT_FOUND, "Flow chart not found: " + id);
        }
        final SimulationSession session = simulations.remove(id);
        if (session != null) {
            session.simulation.forceStop();
        }
    }

    public List<ModuleClassDto> getModuleClasses() {
        return new ArrayList<>(moduleClasses.values());
    }

    public ModuleDto addModule(final int flowChartId, final AddModuleRequest request) {
        Objects.requireNonNull(request);
        if (request.className() == null || !moduleClasses.containsKey(request.className())) {
            throw apiException(Response.Status.BAD_REQUEST, "Unknown module class: " + request.className());
        }
        if (request.position() == null) {
            throw apiException(Response.Status.BAD_REQUEST, "A module position is required");
        }
        final IFlowChart flowChart = requireFlowChart(flowChartId);
        final Set<Integer> moduleIdsBefore = moduleIds(flowChart);
        final boolean added = facade.addModule(
                flowChart,
                dynamicClassLoader,
                request.className(),
                request.position().toPoint());
        if (!added) {
            throw apiException(Response.Status.BAD_REQUEST, "Unable to add module at the requested position");
        }
        final IFlowModule module = findNewModule(flowChart, moduleIdsBefore);
        module.getExternalConfig().setName(module.getClass().getSimpleName() + " " + module.getId());
        return FlowChartMapper.toDto(module);
    }

    public boolean addModule(final AddModuleRequest request) {
        addModule(facade.getMainFlowChart().getId(), request);
        return true;
    }

    public ModuleDto updateModule(final int flowChartId, final int moduleId, final UpdateModuleRequest request) {
        Objects.requireNonNull(request);
        final IFlowModule module = requireModule(requireFlowChart(flowChartId), moduleId);
        if (request.name() != null) {
            module.getExternalConfig().setName(request.name());
        }
        if (request.priority() != null) {
            module.getExternalConfig().setPriority(request.priority());
        }
        if (request.position() != null) {
            module.getGraphicalRepresentation().setPosition(request.position().toPoint());
        }
        if (module.getGraphicalRepresentation() instanceof IModuleGR moduleGR) {
            if (request.scale() != null) {
                moduleGR.setScale(request.scale());
            }
            if (request.iconText() != null) {
                moduleGR.setIconText(request.iconText());
            }
        }
        return FlowChartMapper.toDto(module);
    }

    public void removeModule(final int flowChartId, final int moduleId) {
        if (!facade.removeModule(requireFlowChart(flowChartId), moduleId)) {
            throw apiException(Response.Status.NOT_FOUND, "Module not found: " + moduleId);
        }
    }

    public boolean removeModule(final int moduleId) {
        removeModule(facade.getMainFlowChart().getId(), moduleId);
        return true;
    }

    public ConnectionDto connect(final int flowChartId, final ConnectRequest request) {
        Objects.requireNonNull(request);
        final IFlowChart flowChart = requireFlowChart(flowChartId);
        final IFlowModule fromModule = requireModule(flowChart, request.fromModuleId());
        final IFlowModule toModule = requireModule(flowChart, request.toModuleId());
        final List<IOutputPin> outputs = fromModule.getAllOutputs();
        final List<IInputPin> inputs = toModule.getAllInputs();

        if (request.fromOutputIndex() < 0 || request.fromOutputIndex() >= outputs.size()) {
            throw apiException(Response.Status.BAD_REQUEST, "Output pin index out of range: " + request.fromOutputIndex());
        }
        if (request.toInputIndex() < 0 || request.toInputIndex() >= inputs.size()) {
            throw apiException(Response.Status.BAD_REQUEST, "Input pin index out of range: " + request.toInputIndex());
        }

        final Set<Integer> connectionIdsBefore = connectionIds(flowChart);
        final boolean connected = facade.connect(
                flowChart,
                outputs.get(request.fromOutputIndex()),
                inputs.get(request.toInputIndex()));
        if (!connected) {
            throw apiException(Response.Status.BAD_REQUEST, "Unable to connect the requested pins");
        }
        return FlowChartMapper.toDto(findNewConnection(flowChart, connectionIdsBefore));
    }

    public boolean connect(final ConnectRequest request) {
        connect(facade.getMainFlowChart().getId(), request);
        return true;
    }

    public ConnectionDto updateConnection(final int flowChartId, final int connectionId,
            final UpdateConnectionRequest request) {
        Objects.requireNonNull(request);
        final IConnection connection = requireConnection(requireFlowChart(flowChartId), connectionId);
        if (request.name() != null) {
            connection.setName(request.name());
        }
        return FlowChartMapper.toDto(connection);
    }

    public void removeConnection(final int flowChartId, final int connectionId) {
        if (!facade.removeConnection(requireFlowChart(flowChartId), connectionId)) {
            throw apiException(Response.Status.NOT_FOUND, "Connection not found: " + connectionId);
        }
    }

    public boolean removeConnection(final int connectionId) {
        removeConnection(facade.getMainFlowChart().getId(), connectionId);
        return true;
    }

    public ValidationResult validateMainFlowChart() {
        return validateFlowChart(facade.getMainFlowChart());
    }

    public ValidationResult validateFlowChart(final int flowChartId) {
        return validateFlowChart(requireFlowChart(flowChartId));
    }

    private ValidationResult validateFlowChart(final IFlowChart flowChart) {
        try {
            final IDefaultArrow invalidArrow = flowChart.validate();
            if (invalidArrow == null) {
                return new ValidationResult(true, null, null);
            }
            return new ValidationResult(false, invalidArrow.getId(), null);
        } catch (ValidationException e) {
            return new ValidationResult(false, null, e.getMessage());
        } catch (RuntimeException e) {
            return new ValidationResult(false, null, "Validation failed: " + e.getMessage());
        }
    }

    public String exportMainFlowChartXml() {
        return exportFlowChartXml(facade.getMainFlowChart().getId());
    }

    public String exportFlowChartXml(final int flowChartId) {
        final IFlowChart flowChart = requireFlowChart(flowChartId);
        synchronized (facade) {
            try {
                IDOM.resetDocument();
                final FlowDOM flowDOM = new FlowDOM(flowChart.getDOM());
                return new DocumentString(flowDOM.getDocument()).toString();
            } finally {
                IDOM.resetDocument();
            }
        }
    }

    public FlowChartDto importMainFlowChartXml(final String xml) {
        if (xml == null || xml.isBlank()) {
            throw apiException(Response.Status.BAD_REQUEST, "An XML request body is required");
        }
        File temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile("flow-chart-", ".xml").toFile();
            Files.writeString(temporaryFile.toPath(), xml);
            final IStoreLoadFacade storeLoad = new StoreLoadFacade(moduleClassesList, dynamicClassLoader);
            final IFlowChart flowChart = storeLoad.loadFlowChart(temporaryFile);
            if (flowChart == null) {
                throw apiException(Response.Status.BAD_REQUEST, "The XML document does not contain a valid flow chart");
            }
            facade.setMainFlowChart(flowChart);
            simulations.clear();
            return FlowChartMapper.toDto(flowChart);
        } catch (IOException e) {
            throw apiException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to process the XML document: " + e.getMessage());
        } finally {
            if (temporaryFile != null) {
                temporaryFile.delete();
            }
        }
    }

    public SimulationStatusDto getSimulationStatus(final int flowChartId) {
        return status(requireSimulation(flowChartId));
    }

    public SimulationStatusDto startSimulation(final int flowChartId) {
        final SimulationSession session = requireSimulation(flowChartId);
        if (!session.simulation.isRunning()) {
            session.simulation.start();
        }
        return status(session);
    }

    public SimulationStatusDto pauseSimulation(final int flowChartId) {
        final SimulationSession session = requireSimulation(flowChartId);
        try {
            session.simulation.pause();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw apiException(Response.Status.INTERNAL_SERVER_ERROR, "Interrupted while pausing the simulation");
        }
        return status(session);
    }

    public SimulationStatusDto stopSimulation(final int flowChartId) {
        final SimulationSession session = requireSimulation(flowChartId);
        try {
            session.simulation.stop();
        } catch (FlowException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw apiException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to stop the simulation: " + e.getMessage());
        }
        return status(session);
    }

    public SimulationStatusDto resetSimulation(final int flowChartId) {
        final SimulationSession session = requireSimulation(flowChartId);
        session.simulation.forceStop();
        try {
            session.simulation.stop();
        } catch (FlowException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw apiException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to reset the simulation: " + e.getMessage());
        }
        return status(session);
    }

    public SimulationStatusDto stepSimulation(final int flowChartId, final StepRequest request) {
        final SimulationSession session = requireSimulation(flowChartId);
        if (session.simulation.isRunning()) {
            throw apiException(Response.Status.CONFLICT, "The simulation is running; pause or stop it before stepping manually");
        }
        int steps = request == null || request.steps() == null ? 1 : request.steps();
        if (steps < 1 || steps > 100_000) {
            throw apiException(Response.Status.BAD_REQUEST, "The number of steps must be between 1 and 100000");
        }
        try {
            for (int i = 0; i < steps; i++) {
                session.simulation.stepOnce();
            }
        } catch (FlowException e) {
            throw apiException(Response.Status.BAD_REQUEST, "Simulation step failed: " + e.getMessage());
        }
        return status(session);
    }

    public SimulationStatusDto stepForwardSimulation(final int flowChartId, final StepForwardRequest request) {
        Objects.requireNonNull(request);
        final SimulationSession session = requireSimulation(flowChartId);
        if (session.simulation.isRunning()) {
            throw apiException(Response.Status.CONFLICT, "The simulation is running; pause or stop it before stepping manually");
        }
        if (request.timeSeconds() <= 0.0) {
            throw apiException(Response.Status.BAD_REQUEST, "The time to advance must be greater than zero");
        }
        session.simulation.stepForward(Time.getFractionConstant(new Fraction(request.timeSeconds())));
        return status(session);
    }

    public SimulationStatusDto configureSimulation(final int flowChartId, final SimulationConfigDto request) {
        final SimulationSession session = requireSimulation(flowChartId);
        if (session.simulation.isRunning()) {
            throw apiException(Response.Status.CONFLICT, "The simulation must be stopped before changing its configuration");
        }
        final ISimulationConfiguration configuration = toConfiguration(request);
        session.simulation.setConfig(configuration);
        session.config = configuration;
        return status(session);
    }

    private SimulationSession requireSimulation(final int flowChartId) {
        final IFlowChart flowChart = requireFlowChart(flowChartId);
        return simulations.computeIfAbsent(flowChartId, id -> {
            final ISimulationConfiguration configuration = defaultConfiguration();
            return new SimulationSession(id, facade.getSimulation(flowChart, configuration), configuration);
        });
    }

    private SimulationStatusDto status(final SimulationSession session) {
        final ISimulation simulation = session.simulation;
        final IStepper stepper = simulation.getStepper();
        final boolean running = simulation.isRunning();
        final boolean paused = stepper == null || stepper.isPaused();
        final int stepCount = stepper == null ? 0 : stepper.getStepCount();
        final double timePassed = stepper == null || stepper.getTimePassed() == null
                ? 0.0
                : stepper.getTimePassed().value.doubleValue();
        final Frequency<Fraction> stepperFrequency = stepper == null ? null : stepper.getFrequency();
        final Double frequencyHz = stepperFrequency == null ? null : stepperFrequency.value.doubleValue();
        return new SimulationStatusDto(
                session.flowChartId,
                running,
                paused,
                stepCount,
                timePassed,
                frequencyHz,
                toConfigurationDto(session.config));
    }

    private ISimulationConfiguration defaultConfiguration() {
        return new SimulationConfiguration(Time.getDoubleConstant(60.0), false);
    }

    private ISimulationConfiguration toConfiguration(final SimulationConfigDto request) {
        if (request == null) {
            return defaultConfiguration();
        }
        final double timeoutSeconds = request.timeoutSeconds() == null ? 60.0 : request.timeoutSeconds();
        if (timeoutSeconds <= 0.0) {
            throw apiException(Response.Status.BAD_REQUEST, "The timeout must be greater than zero");
        }
        try {
            if (request.frequencyNumerator() != null || request.frequencyDenominator() != null) {
                final long numerator = request.frequencyNumerator() == null ? 1L : request.frequencyNumerator();
                final long denominator = request.frequencyDenominator() == null ? 1L : request.frequencyDenominator();
                final Frequency<Fraction> frequency = Frequency.getFractionConstant(new Fraction(numerator, denominator));
                return new SimulationConfiguration(
                        frequency,
                        Time.getDoubleConstant(timeoutSeconds),
                        Boolean.TRUE.equals(request.realtime()));
            }
            return new SimulationConfiguration(
                    Time.getDoubleConstant(timeoutSeconds),
                    Boolean.TRUE.equals(request.realtime()));
        } catch (IllegalArgumentException e) {
            throw apiException(Response.Status.BAD_REQUEST, "Invalid simulation frequency: " + e.getMessage());
        }
    }

    private SimulationConfigDto toConfigurationDto(final ISimulationConfiguration configuration) {
        if (configuration == null) {
            return null;
        }
        final Frequency<Fraction> frequency = configuration.getStepperFrequency();
        final Time<Double> timeout = configuration.getTimeout();
        return new SimulationConfigDto(
                timeout == null ? null : timeout.value,
                configuration.isRealtime(),
                frequency == null ? null : frequency.value.getNumerator(),
                frequency == null ? null : frequency.value.getDenominator());
    }

    private void loadModuleCatalog() {
        final List<File> jars = parseModuleJars();
        try {
            moduleClassesList = new ModuleClassesList(dynamicClassLoader, true, jars.toArray(File[]::new));
        } catch (ClassNotFoundException | IOException e) {
            LOG.warnf("Unable to load configured module jars: %s", e.getMessage());
            moduleClassesList = new ModuleClassesList(dynamicClassLoader, jars.toArray(File[]::new));
        }
        moduleClasses.clear();
        addModuleClass(DynamicObjectLoader.MockModule.class, true);
        for (final Class<? extends IFlowModule> moduleClass : moduleClassesList.getModuleClassesList()) {
            addModuleClass(moduleClass, false);
        }
    }

    private void addModuleClass(final Class<? extends IFlowModule> moduleClass, final boolean builtIn) {
        moduleClasses.put(moduleClass.getName(), new ModuleClassDto(
                moduleClass.getName(),
                moduleClass.getSimpleName(),
                moduleClass.getSimpleName(),
                builtIn));
    }

    private List<File> parseModuleJars() {
        final List<File> jars = new ArrayList<>();
        final String configuredJars = ConfigProvider.getConfig()
                .getOptionalValue("flow.modules.jars", String.class)
                .orElse("");
        for (final String entry : configuredJars.split(",")) {
            final String path = entry.trim();
            if (!path.isEmpty()) {
                final File file = new File(path);
                if (file.isFile()) {
                    jars.add(file);
                } else {
                    LOG.warnf("Configured module jar does not exist or is not a file: %s", path);
                }
            }
        }
        return jars;
    }

    private IFlowChart requireFlowChart(final int id) {
        final IFlowChart flowChart = facade.getFlowChartById(id);
        if (flowChart == null) {
            throw apiException(Response.Status.NOT_FOUND, "Flow chart not found: " + id);
        }
        return flowChart;
    }

    private IFlowModule requireModule(final IFlowChart flowChart, final int moduleId) {
        return flowChart.getModules().stream()
                .filter(module -> module.getId() == moduleId)
                .findFirst()
                .orElseThrow(() -> apiException(Response.Status.NOT_FOUND, "Module not found: " + moduleId));
    }

    private IConnection requireConnection(final IFlowChart flowChart, final int connectionId) {
        return flowChart.getConnections().stream()
                .filter(connection -> connection.getId() == connectionId)
                .findFirst()
                .orElseThrow(() -> apiException(Response.Status.NOT_FOUND, "Connection not found: " + connectionId));
    }

    private IFlowModule findNewModule(final IFlowChart flowChart, final Set<Integer> idsBefore) {
        return flowChart.getModules().stream()
                .filter(module -> !idsBefore.contains(module.getId()))
                .findFirst()
                .orElseThrow(() -> apiException(Response.Status.INTERNAL_SERVER_ERROR, "The new module could not be located"));
    }

    private IConnection findNewConnection(final IFlowChart flowChart, final Set<Integer> idsBefore) {
        return flowChart.getConnections().stream()
                .filter(connection -> !idsBefore.contains(connection.getId()))
                .findFirst()
                .orElseThrow(() -> apiException(Response.Status.INTERNAL_SERVER_ERROR, "The new connection could not be located"));
    }

    private Set<Integer> moduleIds(final IFlowChart flowChart) {
        final Set<Integer> ids = new LinkedHashSet<>();
        flowChart.getModules().stream().map(IFlowChartElement::getId).forEach(ids::add);
        return ids;
    }

    private Set<Integer> connectionIds(final IFlowChart flowChart) {
        final Set<Integer> ids = new LinkedHashSet<>();
        flowChart.getConnections().stream().map(IFlowChartElement::getId).forEach(ids::add);
        return ids;
    }

    private WebApplicationException apiException(final Response.Status status, final String message) {
        return new WebApplicationException(message, Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(status.getStatusCode(), status.getReasonPhrase(), message, null))
                .build());
    }

    private static final class SimulationSession {
        private final int flowChartId;
        private final ISimulation simulation;
        private ISimulationConfiguration config;

        private SimulationSession(final int flowChartId, final ISimulation simulation,
                final ISimulationConfiguration config) {
            this.flowChartId = flowChartId;
            this.simulation = simulation;
            this.config = config;
        }
    }
}
