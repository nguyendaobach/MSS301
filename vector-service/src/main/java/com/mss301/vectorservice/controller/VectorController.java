package com.mss301.vectorservice.controller;

import com.mss301.vectorservice.dto.RetrievedChunkDto;
import com.mss301.vectorservice.dto.request.EmbedRequest;
import com.mss301.vectorservice.dto.request.RetrievalRequest;
import com.mss301.vectorservice.dto.response.ApiResponse;
import com.mss301.vectorservice.dto.response.EmbedResponse;
import com.mss301.vectorservice.service.VectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vectors")
@RequiredArgsConstructor
@Slf4j
public class VectorController {

    private final VectorService vectorService;

    @PostMapping("/embed-and-store")
    public ResponseEntity<ApiResponse<?>> embedAndStore(
            @RequestBody EmbedRequest request) {

        log.info("Received embedding request for {} chunks",
                request.chunks().size());

        try {
            vectorService.embedAndStore(
                    request.chunks(),
                    request.metadata()
            );

            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Chunks embedded and stored successfully in PostgreSQL")
                    .data(request.chunks().size())
                    .build());
        } catch (Exception e) {
            log.error("Error embedding chunks", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.builder()
                            .success(false)
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/retrieve")
    public ResponseEntity<ApiResponse<List<RetrievedChunkDto>>> retrieveChunks(
            @RequestBody RetrievalRequest request) {

        log.info("Retrieving chunks for user: {}, query: {}",
                request.userId(), request.query());

        List<RetrievedChunkDto> chunks;

        if (request.documentIds() != null && !request.documentIds().isEmpty()) {
            // Retrieve from specific documents
            chunks = vectorService.retrieveByDocuments(
                    request.query(),
                    request.userId(),
                    request.documentIds(),
                    request.topK() != null ? request.topK() : 5
            );
        } else {
            // Retrieve from all user's documents
            chunks = vectorService.retrieveRelevantChunks(
                    request.query(),
                    request.userId(),
                    request.topK() != null ? request.topK() : 5
            );
        }

        return ResponseEntity.ok(
                ApiResponse.<List<RetrievedChunkDto>>builder()
                        .success(true)
                        .message("Chunks retrieved successfully in PostgreSQL")
                        .data(chunks)
                .build()
        );
    }

    @DeleteMapping("/document/{documentId}")
    public ResponseEntity<ApiResponse<?>> deleteDocument(@PathVariable String documentId) {
        log.info("Deleting vectors for document: {}", documentId);
        vectorService.deleteDocumentVectors(documentId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Vectors deleted successfully")
                        .build()
        );
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable String userId) {
        log.info("Getting vector statistics for user: {}", userId);
        Map<String, Object> stats = vectorService.getVectorStats(userId);
        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .success(true)
                        .message("Vector statistics retrieved successfully")
                        .data(stats)
                .build()
        );
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Vector Service is running with PostgreSQL + pgvector")
                        .build()
        );
    }
}