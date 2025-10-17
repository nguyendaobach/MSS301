package com.mss301.vectorservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "vector_embeddings", indexes = {
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_document_id", columnList = "documentId"),
        @Index(name = "idx_user_document", columnList = "userId,documentId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vectorId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String documentId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private Integer chunkIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column
    private Integer wordCount;

    // pgvector column - stores vector as array
    @Column(nullable = false, columnDefinition = "vector(1536)")
    @Type(value = com.mss301.vectorservice.utility.VectorType.class)
    private Double[] embedding;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
