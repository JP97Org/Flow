package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "UpdateConnectionPathRequest", description = "New diversion points for one connection target.")
public record UpdateConnectionPathRequest(
        PinReferenceDto to,
        List<PointDto> diversionPoints) {
}
