package com.mss301.vectorservice.service.impl;

import com.mss301.vectorservice.dto.DocumentMetadataDto;
import com.mss301.vectorservice.dto.RetrievedChunkDto;
import com.mss301.vectorservice.dto.TextChunkDto;
import com.mss301.vectorservice.entity.VectorEmbedding;
import com.mss301.vectorservice.repository.VectorEmbeddingRepository;
import com.mss301.vectorservice.service.VectorService;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorServiceImpl implements VectorService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${vector.similarity.threshold:0.7}")
    private Double similarityThreshold;

    private final VectorEmbeddingRepository vectorRepository;
    private OpenAiService openAiService;

    @PostConstruct
    public void init() {
        log.info("Initializing Vector Service with PostgreSQL + pgvector...");
        openAiService = new OpenAiService(openaiApiKey);
        log.info("OpenAI service initialized");
    }

    @Transactional
    public void embedAndStore(List<TextChunkDto> chunks, DocumentMetadataDto metadata) {
        log.info("Processing {} chunks for document: {}",
                chunks.size(), metadata.documentId());

        List<VectorEmbedding> embeddings = new ArrayList<>();

        for (TextChunkDto chunk : chunks) {
            try {
                // 1. Create embedding using OpenAI
                List<Double> embedding = createEmbedding(chunk.text());

                // 2. Convert to pgvector format (array of doubles)
                Double[] embeddingArray = embedding.toArray(new Double[0]);

                // 3. Create entity
                VectorEmbedding vectorEmbedding = VectorEmbedding.builder()
                        .vectorId(UUID.randomUUID().toString())
                        .userId(metadata.userId())
                        .documentId(metadata.documentId())
                        .fileName(metadata.fileName())
                        .chunkIndex(chunk.index())
                        .text(chunk.text())
                        .wordCount(chunk.wordCount())
                        .embedding(embeddingArray)
                        .createdAt(LocalDateTime.now())
                        .build();

                embeddings.add(vectorEmbedding);

                log.debug("Chunk {} embedded successfully", chunk.index());

            } catch (Exception e) {
                log.error("Error embedding chunk {}", chunk.index(), e);
                throw new RuntimeException("Failed to embed chunk", e);
            }
        }

        // 4. Batch save to PostgreSQL
        try {
            vectorRepository.saveAll(embeddings);
            log.info("Successfully saved {} embeddings to PostgreSQL", embeddings.size());
        } catch (Exception e) {
            log.error("Error saving embeddings to database", e);
            throw new RuntimeException("Failed to store embeddings", e);
        }
    }

    private List<Double> createEmbedding(String text) {
        try {
            EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                    .model("text-embedding-3-small")
                    .input(Collections.singletonList(text))
                    .build();

            EmbeddingResult result = openAiService.createEmbeddings(embeddingRequest);

            return result.getData().get(0).getEmbedding();

        } catch (Exception e) {
            log.error("Error creating embedding", e);
            throw new RuntimeException("Failed to create embedding", e);
        }
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunkDto> retrieveRelevantChunks(
            String query,
            String userId,
            int topK) {

        log.info("Retrieving top {} chunks for query: '{}', user: {}",
                topK, query, userId);

        try {
            // 1. Create query embedding
            List<Double> queryEmbedding = createEmbedding(query);
            Double[] queryVector = queryEmbedding.toArray(new Double[0]);

            // 2. Use cosine similarity search with pgvector
            // pgvector uses <=> operator for cosine distance (1 - cosine similarity)
            List<VectorEmbedding> results = vectorRepository
                    .findTopKSimilarByUserIdAndVector(userId, queryVector, topK);

            // 3. Calculate similarity scores and filter by threshold
            List<RetrievedChunkDto> retrievedChunks = results.stream()
                    .map(embedding -> {
                        // Calculate cosine similarity
                        double similarity = calculateCosineSimilarity(
                                queryVector,
                                embedding.getEmbedding()
                        );

                        return RetrievedChunkDto.builder()
                                .text(embedding.getText())
                                .score((float) similarity)
                                .fileName(embedding.getFileName())
                                .documentId(embedding.getDocumentId())
                                .chunkIndex(embedding.getChunkIndex())
                                .build();
                    })
                    .filter(chunk -> chunk.score() >= similarityThreshold)
                    .collect(Collectors.toList());

            log.info("Retrieved {} relevant chunks (threshold: {})",
                    retrievedChunks.size(), similarityThreshold);

            return retrievedChunks;

        } catch (Exception e) {
            log.error("Error retrieving chunks", e);
            throw new RuntimeException("Failed to retrieve chunks", e);
        }
    }

    private double calculateCosineSimilarity(Double[] vectorA, Double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Transactional
    public void deleteDocumentVectors(String documentId) {
        try {
            int deletedCount = vectorRepository.deleteByDocumentId(documentId);
            log.info("Deleted {} vectors for document: {}", deletedCount, documentId);
        } catch (Exception e) {
            log.error("Error deleting document vectors", e);
            throw new RuntimeException("Failed to delete vectors", e);
        }
    }

    @Transactional(readOnly = true)
    public List<RetrievedChunkDto> retrieveByDocuments(
            String query,
            String userId,
            List<String> documentIds,
            int topK) {

        log.info("Retrieving chunks from specific documents: {}", documentIds);

        try {
            List<Double> queryEmbedding = createEmbedding(query);
            Double[] queryVector = queryEmbedding.toArray(new Double[0]);

            List<VectorEmbedding> results = vectorRepository
                    .findTopKSimilarByUserIdAndDocumentsAndVector(
                            userId,
                            documentIds,
                            queryVector,
                            topK
                    );

            return results.stream()
                    .map(embedding -> {
                        double similarity = calculateCosineSimilarity(
                                queryVector,
                                embedding.getEmbedding()
                        );

                        return RetrievedChunkDto.builder()
                                .text(embedding.getText())
                                .score((float) similarity)
                                .fileName(embedding.getFileName())
                                .documentId(embedding.getDocumentId())
                                .chunkIndex(embedding.getChunkIndex())
                                .build();
                    })
                    .filter(chunk -> chunk.score() >= similarityThreshold)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error retrieving chunks from documents", e);
            throw new RuntimeException("Failed to retrieve chunks", e);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getVectorStats(String userId) {
        Map<String, Object> stats = new HashMap<>();

        long totalVectors = vectorRepository.countByUserId(userId);
        long totalDocuments = vectorRepository.countDistinctDocumentsByUserId(userId);

        stats.put("totalVectors", totalVectors);
        stats.put("totalDocuments", totalDocuments);
        stats.put("userId", userId);

        return stats;
    }
}