package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Health", description = "Basic application health information.")
public record HealthDto(String status, String application, String version) {
}
