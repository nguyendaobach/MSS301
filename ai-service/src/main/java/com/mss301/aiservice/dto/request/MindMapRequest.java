package com.mss301.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MindMapRequest {
//        @NotBlank(message = "User ID is required")
        private String userId;
        @NotBlank(message = "Prompt is required")
        private String prompt;
        private Integer maxDepth;    // Max levels of the mindmap
        private Integer minNodes;     // Minimum number of nodes
        private List<String> focusAreas; // Specific topics to focus on
        private String language    ;    // Output language (default: Vietnamese)
}
