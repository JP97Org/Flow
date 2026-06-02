package org.jojo.flow.rest;

import jakarta.inject.Inject;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/flow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModelFacadeResource {

    @Inject
    ModelFacadeService service;

    @GET
    @Path("/main")
    public Response getMainFlowChart() {
        final FlowChartSummary summary = service.getMainFlowChartSummary();
        return Response.ok(summary).build();
    }

    @GET
    @Path("/{id}")
    public Response getFlowChartById(@PathParam("id") final int id) {
        final FlowChartSummary summary = service.getFlowChartSummary(id);
        return summary == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(summary).build();
    }

    @GET
    @Path("/next-id")
    public int nextId() {
        return service.nextFreeId();
    }

    @POST
    @Path("/main")
    public FlowChartSummary createMainFlowChart() {
        return service.createNewMainFlowChart();
    }

    @POST
    @Path("/module")
    public Response addModule(final AddModuleRequest request) {
        return service.addModule(request) ? Response.accepted().build() : Response.status(Response.Status.BAD_REQUEST).entity("Unable to add module").build();
    }

    @DELETE
    @Path("/module/{id}")
    public Response removeModule(@PathParam("id") final int id) {
        return service.removeModule(id) ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/connect")
    public Response connect(final ConnectRequest request) {
        return service.connect(request) ? Response.accepted().build() : Response.status(Response.Status.BAD_REQUEST).entity("Unable to connect pins").build();
    }

    @DELETE
    @Path("/connection/{id}")
    public Response removeConnection(@PathParam("id") final int id) {
        return service.removeConnection(id) ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/validate")
    public ValidationResult validateMainFlowChart() {
        return service.validateMainFlowChart();
    }

    @GET
    @Path("/{id}/validate")
    public ValidationResult validateFlowChart(@PathParam("id") final int id) {
        return service.validateFlowChart(id);
    }

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of("status", "UP")).build();
    }
}
