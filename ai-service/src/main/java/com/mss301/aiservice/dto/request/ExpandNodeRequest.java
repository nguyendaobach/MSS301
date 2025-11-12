package com.mss301.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ExpandNodeRequest(
        @NotBlank
        String userId,

        @NotBlank
        String nodeId,

        Integer numberOfChildren,

        List<String> documentIds // Specific documents to use for context (optional)
) {
}
