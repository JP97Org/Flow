package org.jojo.flow.rest.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ModuleJarUploadResult", description = "Module types loaded from an uploaded module jar.")
public record ModuleJarUploadResultDto(String jarFileName, List<ModuleClassDto> moduleClasses) {
}
