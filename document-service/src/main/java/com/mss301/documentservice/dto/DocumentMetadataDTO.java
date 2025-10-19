package com.mss301.documentservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DocumentMetadataDTO(
        String documentId,
        String userId,
        String fileName,
        Long fileSize,
        String contentType,
        Integer chunkCount,
        LocalDateTime uploadedAt
) {
}
