package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "UpdateConnectionRequest", description = "Mutable properties of a connection.")
public record UpdateConnectionRequest(String name) {
}
