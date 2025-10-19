package com.mss301.vectorservice.dto;

import java.time.LocalDateTime;

public record DocumentMetadataDto(
        String documentId,
        String userId,
        String fileName,
        Long fileSize,
        String contentType,
        Integer chunkCount,
        LocalDateTime uploadedAt
) {
}
