package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "StepForwardRequest", description = "Simulation time to advance in seconds.")
public record StepForwardRequest(double timeSeconds) {
}
