package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "PinReference", description = "Identifies a pin by its module and its index in the module's input or output list.")
public record PinReferenceDto(int moduleId, int index, String direction) {
}
