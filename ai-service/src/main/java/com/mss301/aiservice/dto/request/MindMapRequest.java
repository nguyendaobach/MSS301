package com.mss301.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record MindMapRequest(
        @NotBlank(message = "User ID is required")
        String userId,
        @NotBlank(message = "Prompt is required")
        String prompt,
        Integer maxDepth,      // Max levels of the mindmap
        Integer minNodes,      // Minimum number of nodes
        List<String> focusAreas, // Specific topics to focus on
        String language        // Output language (default: Vietnamese)
) {
}
