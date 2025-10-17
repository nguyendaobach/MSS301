package com.mss301.documentservice.repository;

import com.mss301.documentservice.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, Long> {

    List<DocumentMetadata> findByUserId(String userId);

    Optional<DocumentMetadata> findByDocumentIdAndUserId(String documentId, String userId);

    Optional<DocumentMetadata> findByDocumentId(String documentId);
}