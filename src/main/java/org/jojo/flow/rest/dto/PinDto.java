package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Pin", description = "A module pin including its data signature and graphical information.")
public record PinDto(
        int index,
        String direction,
        String pinClass,
        String implementationClass,
        String dataSignature,
        String defaultData,
        PointDto position,
        PointDto linePoint,
        Integer width,
        Integer height,
        String iconText,
        String orientation,
        List<Integer> connectionIds) {
}
