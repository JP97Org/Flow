package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "UpdateModuleRequest", description = "Mutable properties of a module. Null values keep the current value.")
public record UpdateModuleRequest(String name, Integer priority, PointDto position, Double scale, String iconText) {
}
