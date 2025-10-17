package com.mss301.vectorservice.service;

import com.mss301.vectorservice.dto.DocumentMetadataDto;
import com.mss301.vectorservice.dto.RetrievedChunkDto;
import com.mss301.vectorservice.dto.TextChunkDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface VectorService {
    void embedAndStore(List<TextChunkDto> chunks, DocumentMetadataDto metadata);
    List<RetrievedChunkDto> retrieveRelevantChunks(
            String query,
            String userId,
            int topK
    );
    void deleteDocumentVectors(String documentId);
    List<RetrievedChunkDto> retrieveByDocuments(
            String query,
            String userId,
            List<String> documentIds,
            int topK
    );
    Map<String, Object> getVectorStats(String userId);
}
