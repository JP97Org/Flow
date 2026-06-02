package org.jojo.flow.rest;

public record ValidationResult(boolean valid, Integer invalidArrowId, String errorMessage) {
}
