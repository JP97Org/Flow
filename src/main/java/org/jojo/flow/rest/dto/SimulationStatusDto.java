package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "SimulationStatus", description = "Current simulation state for a flow chart.")
public record SimulationStatusDto(
        int flowChartId,
        boolean running,
        boolean paused,
        int stepCount,
        double timePassedSeconds,
        Double frequencyHz,
        SimulationConfigDto config) {
}
