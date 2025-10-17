package com.mss301.aiservice.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record RetrievalRequest(
        String query,
        String userId,
        Integer topK,
        List<String> documentIds
) {
}
