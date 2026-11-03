package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "SimulationConfig", description = "Simulation configuration. A null frequency lets the backend choose the minimal time step automatically.")
public record SimulationConfigDto(
        Double timeoutSeconds,
        Boolean realtime,
        Long frequencyNumerator,
        Long frequencyDenominator) {
}
