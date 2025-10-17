package com.mss301.vectorservice.dto.request;

import java.util.List;

public record RetrievalRequest(
        String query,
        String userId,
        Integer topK,
        List<String> documentIds
) {
}
