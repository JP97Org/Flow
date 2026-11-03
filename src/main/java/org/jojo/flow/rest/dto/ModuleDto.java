package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Module", description = "A flow module with its configuration, pins and warnings.")
public record ModuleDto(
        int id,
        String className,
        String name,
        int priority,
        PointDto position,
        int width,
        int height,
        double scale,
        String iconText,
        String infoText,
        List<PinDto> inputs,
        List<PinDto> outputs,
        List<WarningDto> warnings) {
}
