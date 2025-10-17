package com.mss301.documentservice.controller;

import com.mss301.documentservice.dto.DocumentMetadataDTO;
import com.mss301.documentservice.service.DocumentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentMetadataDTO> uploadDocument(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("userId") @NotNull String userId) {

        log.info("Received document upload request from user: {}, filename: {}",
                userId, file.getOriginalFilename());

        try {
            DocumentMetadataDTO metadata = documentService.processDocument(file, userId);
            log.info("Document processed successfully: {}", metadata.documentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(metadata);
        } catch (Exception e) {
            log.error("Error processing document", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentMetadataDTO>> getUserDocuments(
            @PathVariable String userId) {

        log.info("Fetching documents for user: {}", userId);
        List<DocumentMetadataDTO> documents = documentService.getUserDocuments(userId);
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String documentId,
            @RequestParam String userId) {

        log.info("Deleting document: {} for user: {}", documentId, userId);
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Document Service is running");
    }
}
