package com.mss301.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegenerateNodeRequest(
        @NotBlank
        String userId,

        @NotBlank
        String nodeId,

        String additionalContext
) {
}
