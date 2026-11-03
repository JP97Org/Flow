package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "AddModuleRequest", description = "Request to add a module to a flow chart.")
public record AddModuleRequest(String className, PointDto position) {
}
