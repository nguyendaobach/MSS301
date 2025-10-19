package com.mss301.documentservice.mapper;

import com.mss301.documentservice.dto.DocumentMetadataDTO;
import com.mss301.documentservice.entity.DocumentMetadata;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    public DocumentMetadataDTO toDto(DocumentMetadata entity) {
        return DocumentMetadataDTO.builder()
                .documentId(entity.getDocumentId())
                .userId(entity.getUserId())
                .fileName(entity.getFileName())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .chunkCount(entity.getChunkCount())
                .uploadedAt(entity.getUploadedAt())
                .build();
    }
}
