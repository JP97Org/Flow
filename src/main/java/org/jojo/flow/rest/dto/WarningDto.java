package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Warning", description = "A warning or error reported by a flow chart element.")
public record WarningDto(int id, String description, boolean error, Integer affectedElementId) {
}
