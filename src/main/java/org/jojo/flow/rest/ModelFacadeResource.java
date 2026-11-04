package org.jojo.flow.rest;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jojo.flow.rest.dto.AddModuleRequest;
import org.jojo.flow.rest.dto.ConnectRequest;
import org.jojo.flow.rest.dto.ConnectionDto;
import org.jojo.flow.rest.dto.FlowChartDto;
import org.jojo.flow.rest.dto.FlowChartSummary;
import org.jojo.flow.rest.dto.HealthDto;
import org.jojo.flow.rest.dto.ModuleClassDto;
import org.jojo.flow.rest.dto.ModuleDto;
import org.jojo.flow.rest.dto.SimulationConfigDto;
import org.jojo.flow.rest.dto.SimulationStatusDto;
import org.jojo.flow.rest.dto.StepForwardRequest;
import org.jojo.flow.rest.dto.StepRequest;
import org.jojo.flow.rest.dto.UpdateConnectionPathRequest;
import org.jojo.flow.rest.dto.UpdateConnectionRequest;
import org.jojo.flow.rest.dto.UpdateModuleRequest;
import org.jojo.flow.rest.dto.ValidationResult;

@Path("/api/flow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Flow chart", description = "Create, edit, validate, simulate and persist flow charts.")
public class ModelFacadeResource {

    @Inject
    ModelFacadeService service;

    @GET
    @Path("/health")
    @Operation(operationId = "getHealth", summary = "Get application health")
    public HealthDto health() {
        return new HealthDto("UP", "flow", "1.0.0");
    }

    @GET
    @Path("/next-id")
    @Operation(operationId = "getNextFreeId", summary = "Get the next free global element ID")
    public int nextId() {
        return service.nextFreeId();
    }

    @GET
    @Path("/module-classes")
    @Operation(operationId = "getModuleClasses", summary = "List all available module types")
    public List<ModuleClassDto> getModuleClasses() {
        return service.getModuleClasses();
    }

    @GET
    @Path("/charts")
    @Operation(operationId = "getFlowCharts", summary = "List all flow charts")
    public List<FlowChartSummary> getFlowCharts() {
        return service.getFlowCharts();
    }

    @POST
    @Path("/charts")
    @Operation(operationId = "createFlowChart", summary = "Create an additional empty flow chart")
    public FlowChartDto createFlowChart() {
        return service.createFlowChart();
    }

    @DELETE
    @Path("/{id}")
    @Operation(operationId = "deleteFlowChart", summary = "Delete a non-main flow chart")
    @APIResponse(responseCode = "204", description = "Flow chart deleted")
    public void deleteFlowChart(@PathParam("id") final int id) {
        service.removeFlowChart(id);
    }

    @GET
    @Path("/main")
    @Operation(operationId = "getMainFlowChart", summary = "Get the main flow chart")
    public FlowChartDto getMainFlowChart() {
        return service.getMainFlowChart();
    }

    @GET
    @Path("/main/summary")
    @Operation(operationId = "getMainFlowChartSummary", summary = "Get the main flow chart element ID summary")
    public FlowChartSummary getMainFlowChartSummary() {
        return service.getMainFlowChartSummary();
    }

    @POST
    @Path("/main")
    @Operation(operationId = "createMainFlowChart", summary = "Replace the main flow chart with a new empty chart")
    public FlowChartDto createMainFlowChart() {
        return service.createNewMainFlowChart();
    }

    @GET
    @Path("/{id}")
    @Operation(operationId = "getFlowChart", summary = "Get a complete flow chart")
    public FlowChartDto getFlowChart(@PathParam("id") final int id) {
        return service.getFlowChart(id);
    }

    @GET
    @Path("/{id}/summary")
    @Operation(operationId = "getFlowChartSummary", summary = "Get a flow chart element ID summary")
    public FlowChartSummary getFlowChartSummary(@PathParam("id") final int id) {
        return service.getFlowChartSummary(id);
    }

    @POST
    @Path("/{id}/modules")
    @Operation(operationId = "addModuleToFlowChart", summary = "Add a module to a flow chart")
    public ModuleDto addModuleToFlowChart(@PathParam("id") final int id, final AddModuleRequest request) {
        return service.addModule(id, request);
    }

    @PUT
    @Path("/{id}/modules/{moduleId}")
    @Operation(operationId = "updateModule", summary = "Update module name, priority, position, scale or icon text")
    public ModuleDto updateModule(@PathParam("id") final int id, @PathParam("moduleId") final int moduleId,
            final UpdateModuleRequest request) {
        return service.updateModule(id, moduleId, request);
    }

    @DELETE
    @Path("/{id}/modules/{moduleId}")
    @Operation(operationId = "removeModuleFromFlowChart", summary = "Remove a module from a flow chart")
    @APIResponse(responseCode = "204", description = "Module removed")
    public void removeModuleFromFlowChart(@PathParam("id") final int id,
            @PathParam("moduleId") final int moduleId) {
        service.removeModule(id, moduleId);
    }

    @POST
    @Path("/module")
    @Operation(operationId = "addModule", summary = "Add a module to the main flow chart")
    public ModuleDto addModule(final AddModuleRequest request) {
        return service.addModule(service.getMainFlowChart().id(), request);
    }

    @DELETE
    @Path("/module/{id}")
    @Operation(operationId = "removeModule", summary = "Remove a module from the main flow chart")
    @APIResponse(responseCode = "204", description = "Module removed")
    public void removeModule(@PathParam("id") final int id) {
        service.removeModule(id);
    }

    @POST
    @Path("/{id}/connections")
    @Operation(operationId = "connectPins", summary = "Connect an output pin to an input pin")
    public ConnectionDto connectPins(@PathParam("id") final int id, final ConnectRequest request) {
        return service.connect(id, request);
    }

    @PUT
    @Path("/{id}/connections/{connectionId}")
    @Operation(operationId = "updateConnection", summary = "Update a connection name")
    public ConnectionDto updateConnection(@PathParam("id") final int id,
            @PathParam("connectionId") final int connectionId, final UpdateConnectionRequest request) {
        return service.updateConnection(id, connectionId, request);
    }

    @PUT
    @Path("/{id}/connections/{connectionId}/path")
    @Operation(operationId = "updateConnectionPath", summary = "Update the path of a connection target")
    public ConnectionDto updateConnectionPath(@PathParam("id") final int id,
            @PathParam("connectionId") final int connectionId,
            final UpdateConnectionPathRequest request) {
        return service.updateConnectionPath(id, connectionId, request);
    }

    @DELETE
    @Path("/{id}/connections/{connectionId}")
    @Operation(operationId = "removeConnectionFromFlowChart", summary = "Remove a connection from a flow chart")
    @APIResponse(responseCode = "204", description = "Connection removed")
    public void removeConnectionFromFlowChart(@PathParam("id") final int id,
            @PathParam("connectionId") final int connectionId) {
        service.removeConnection(id, connectionId);
    }

    @POST
    @Path("/connect")
    @Operation(operationId = "connectMainFlowChartPins", summary = "Connect pins on the main flow chart")
    public ConnectionDto connect(final ConnectRequest request) {
        return service.connect(service.getMainFlowChart().id(), request);
    }

    @DELETE
    @Path("/connection/{id}")
    @Operation(operationId = "removeConnection", summary = "Remove a connection from the main flow chart")
    @APIResponse(responseCode = "204", description = "Connection removed")
    public void removeConnection(@PathParam("id") final int id) {
        service.removeConnection(id);
    }

    @GET
    @Path("/validate")
    @Operation(operationId = "validateMainFlowChart", summary = "Validate the main flow chart")
    public ValidationResult validateMainFlowChart() {
        return service.validateMainFlowChart();
    }

    @GET
    @Path("/{id}/validate")
    @Operation(operationId = "validateFlowChart", summary = "Validate a flow chart")
    public ValidationResult validateFlowChart(@PathParam("id") final int id) {
        return service.validateFlowChart(id);
    }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @Operation(operationId = "exportMainFlowChartXml", summary = "Export the main flow chart as XML")
    @APIResponse(responseCode = "200", description = "Flow chart XML",
            content = @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(type = SchemaType.STRING)))
    public Response exportMainFlowChartXml() {
        return Response.ok(service.exportMainFlowChartXml(), MediaType.APPLICATION_XML).build();
    }

    @GET
    @Path("/{id}/xml")
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @Operation(operationId = "exportFlowChartXml", summary = "Export a flow chart as XML")
    @APIResponse(responseCode = "200", description = "Flow chart XML",
            content = @Content(mediaType = MediaType.APPLICATION_XML, schema = @Schema(type = SchemaType.STRING)))
    public Response exportFlowChartXml(@PathParam("id") final int id) {
        return Response.ok(service.exportFlowChartXml(id), MediaType.APPLICATION_XML).build();
    }

    @POST
    @Path("/xml")
    @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
    @Operation(operationId = "importMainFlowChartXml", summary = "Load and replace the main flow chart from XML")
    public FlowChartDto importMainFlowChartXml(
            @Parameter(description = "Flow chart XML document", required = true) final String xml) {
        return service.importMainFlowChartXml(xml);
    }

    @GET
    @Path("/{id}/simulation")
    @Operation(operationId = "getSimulationStatus", summary = "Get simulation status")
    public SimulationStatusDto getSimulationStatus(@PathParam("id") final int id) {
        return service.getSimulationStatus(id);
    }

    @POST
    @Path("/{id}/simulation/start")
    @Operation(operationId = "startSimulation", summary = "Start or resume the simulation")
    public SimulationStatusDto startSimulation(@PathParam("id") final int id) {
        return service.startSimulation(id);
    }

    @POST
    @Path("/{id}/simulation/pause")
    @Operation(operationId = "pauseSimulation", summary = "Pause the simulation")
    public SimulationStatusDto pauseSimulation(@PathParam("id") final int id) {
        return service.pauseSimulation(id);
    }

    @POST
    @Path("/{id}/simulation/stop")
    @Operation(operationId = "stopSimulation", summary = "Stop and reset the simulation")
    public SimulationStatusDto stopSimulation(@PathParam("id") final int id) {
        return service.stopSimulation(id);
    }

    @POST
    @Path("/{id}/simulation/reset")
    @Operation(operationId = "resetSimulation", summary = "Reset the simulation")
    public SimulationStatusDto resetSimulation(@PathParam("id") final int id) {
        return service.resetSimulation(id);
    }

    @POST
    @Path("/{id}/simulation/step")
    @Operation(operationId = "stepSimulation", summary = "Perform one or more manual simulation steps")
    public SimulationStatusDto stepSimulation(@PathParam("id") final int id, final StepRequest request) {
        return service.stepSimulation(id, request);
    }

    @POST
    @Path("/{id}/simulation/step-forward")
    @Operation(operationId = "stepForwardSimulation", summary = "Advance the simulation by a time interval")
    public SimulationStatusDto stepForwardSimulation(@PathParam("id") final int id,
            final StepForwardRequest request) {
        return service.stepForwardSimulation(id, request);
    }

    @PUT
    @Path("/{id}/simulation/config")
    @Operation(operationId = "configureSimulation", summary = "Change the simulation configuration")
    public SimulationStatusDto configureSimulation(@PathParam("id") final int id,
            final SimulationConfigDto request) {
        return service.configureSimulation(id, request);
    }
}
