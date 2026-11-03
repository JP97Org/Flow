package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "A machine-readable error response.")
public record ErrorResponse(int status, String error, String message, Integer elementId) {
}
