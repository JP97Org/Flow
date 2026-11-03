package org.jojo.flow.rest.dto;

import java.awt.Point;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Point", description = "A two-dimensional point in flow chart coordinates.")
public record PointDto(int x, int y) {
    public static PointDto from(final Point point) {
        return point == null ? null : new PointDto(point.x, point.y);
    }

    public Point toPoint() {
        return new Point(x, y);
    }
}
