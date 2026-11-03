package org.jojo.flow.rest;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jojo.flow.exc.FlowException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.rest.dto.ErrorResponse;

@Provider
public class FlowExceptionMapper implements ExceptionMapper<FlowException> {
    @Override
    public Response toResponse(final FlowException exception) {
        final Warning warning = exception.getWarning();
        final Integer elementId = warning != null && warning.hasAffectedElement()
                ? warning.getAffectedElement().getId()
                : null;
        final String message = warning == null ? exception.getMessage() : warning.getDescription();
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(
                        Response.Status.BAD_REQUEST.getStatusCode(),
                        Response.Status.BAD_REQUEST.getReasonPhrase(),
                        message,
                        elementId))
                .build();
    }
}
