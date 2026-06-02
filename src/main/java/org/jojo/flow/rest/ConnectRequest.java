package org.jojo.flow.rest;

public record ConnectRequest(int fromModuleId, int fromOutputIndex, int toModuleId, int toInputIndex) {
}
