package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "FlowChartSummary", description = "A compact list of the element IDs contained in a flow chart.")
public record FlowChartSummary(int id, List<Integer> moduleIds, List<Integer> connectionIds) {
}
