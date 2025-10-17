package com.mss301.vectorservice.dto;

import lombok.Builder;

@Builder
public record RetrievedChunkDto(
        String text,
        Float score,
        String fileName,
        String documentId,
        Integer chunkIndex
) {

}