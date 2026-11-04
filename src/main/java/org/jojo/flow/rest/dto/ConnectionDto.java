package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Connection", description = "A connection between an output pin and one or more input pins.")
public record ConnectionDto(
        int id,
        String className,
        String name,
        String info,
        PinReferenceDto from,
        List<PinReferenceDto> to,
        String dataSignature,
        List<ConnectionRouteDto> routes,
        List<WarningDto> warnings) {
}
