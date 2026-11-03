package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "StepRequest", description = "Number of simulation steps to perform. Defaults to one if omitted.")
public record StepRequest(Integer steps) {
}
