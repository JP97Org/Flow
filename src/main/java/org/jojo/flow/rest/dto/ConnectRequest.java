package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ConnectRequest", description = "Request to connect an output pin to an input pin.")
public record ConnectRequest(int fromModuleId, int fromOutputIndex, int toModuleId, int toInputIndex) {
}
