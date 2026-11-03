package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ValidationResult", description = "Result of validating a flow chart.")
public record ValidationResult(boolean valid, Integer invalidArrowId, String errorMessage) {
}
