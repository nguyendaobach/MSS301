package com.mss301.documentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String documentId;

    @Column(nullable = false)
    String userId;

    @Column(nullable = false)
    String fileName;

    Long fileSize;

    String contentType;

    Integer chunkCount;

    @Column(nullable = false)
    LocalDateTime uploadedAt;

    @Column
    LocalDateTime deletedAt;
}