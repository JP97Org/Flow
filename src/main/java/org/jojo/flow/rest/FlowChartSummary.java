package org.jojo.flow.rest;

import java.util.List;

public record FlowChartSummary(int id, List<Integer> moduleIds, List<Integer> connectionIds) {
}
