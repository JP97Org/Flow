package org.jojo.flow.rest.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ModuleClass", description = "A flow module type that can be added to a flow chart.")
public record ModuleClassDto(String className, String simpleName, String displayName, boolean builtIn) {
}
