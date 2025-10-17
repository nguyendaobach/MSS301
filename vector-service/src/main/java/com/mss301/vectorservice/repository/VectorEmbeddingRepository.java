package com.mss301.vectorservice.repository;

import com.mss301.vectorservice.entity.VectorEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VectorEmbeddingRepository extends JpaRepository<VectorEmbedding, Long> {

    /**
     * Find top K most similar vectors using pgvector cosine distance
     * The <=> operator calculates cosine distance (1 - cosine similarity)
     * Lower distance = higher similarity
     */
    @Query(value = "SELECT * FROM vector_embeddings " +
            "WHERE user_id = :userId " +
            "ORDER BY embedding <=> CAST(:queryVector AS vector) " +
            "LIMIT :topK",
            nativeQuery = true)
    List<VectorEmbedding> findTopKSimilarByUserIdAndVector(
            @Param("userId") String userId,
            @Param("queryVector") Double[] queryVector,
            @Param("topK") int topK
    );

    /**
     * Find top K similar vectors from specific documents
     */
    @Query(value = "SELECT * FROM vector_embeddings " +
            "WHERE user_id = :userId " +
            "AND document_id IN :documentIds " +
            "ORDER BY embedding <=> CAST(:queryVector AS vector) " +
            "LIMIT :topK",
            nativeQuery = true)
    List<VectorEmbedding> findTopKSimilarByUserIdAndDocumentsAndVector(
            @Param("userId") String userId,
            @Param("documentIds") List<String> documentIds,
            @Param("queryVector") Double[] queryVector,
            @Param("topK") int topK
    );

    /**
     * Delete all vectors for a document
     */
    @Modifying
    @Query("DELETE FROM VectorEmbedding v WHERE v.documentId = :documentId")
    int deleteByDocumentId(@Param("documentId") String documentId);

    /**
     * Count vectors for a user
     */
    long countByUserId(String userId);

    /**
     * Count distinct documents for a user
     */
    @Query("SELECT COUNT(DISTINCT v.documentId) FROM VectorEmbedding v WHERE v.userId = :userId")
    long countDistinctDocumentsByUserId(@Param("userId") String userId);

    /**
     * Find all vectors for a document
     */
    List<VectorEmbedding> findByDocumentIdOrderByChunkIndex(String documentId);

    /**
     * Find all vectors for a user
     */
    List<VectorEmbedding> findByUserIdOrderByCreatedAtDesc(String userId);
}