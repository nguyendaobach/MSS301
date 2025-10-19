package com.mss301.aiservice.dto;

public record RetrievedChunkDto(
        String text,
        Float score,
        String fileName,
        String documentId,
        Integer chunkIndex
) {
}
