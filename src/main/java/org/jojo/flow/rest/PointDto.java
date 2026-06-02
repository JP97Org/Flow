package org.jojo.flow.rest;

public record PointDto(int x, int y) {
    public java.awt.Point toPoint() {
        return new java.awt.Point(x, y);
    }
}
