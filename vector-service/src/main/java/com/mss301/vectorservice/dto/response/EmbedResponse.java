package com.mss301.vectorservice.dto.response;

import lombok.Builder;

@Builder
public record EmbedResponse(
        boolean success,
        String message,
        Integer chunkCount
) {
}
