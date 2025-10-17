package com.mss301.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExpandNodeRequest(
        @NotBlank
        String userId,

        @NotBlank
        String nodeId,

        Integer numberOfChildren
) {
}
