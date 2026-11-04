package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ConnectionRoute", description = "The path of one connection target, expressed as diversion points.")
public record ConnectionRouteDto(
        PinReferenceDto to,
        List<PointDto> diversionPoints,
        boolean customized) {
}
