package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "FlowChart", description = "A complete flow chart including modules, connections and warnings.")
public record FlowChartDto(
        int id,
        PointDto position,
        int width,
        int height,
        boolean rasterEnabled,
        PointDto absoluteOrigin,
        List<ModuleDto> modules,
        List<ConnectionDto> connections,
        List<WarningDto> warnings) {
}
