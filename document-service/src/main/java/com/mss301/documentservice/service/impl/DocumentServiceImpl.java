package com.mss301.documentservice.service.impl;

import com.mss301.documentservice.dto.DocumentMetadataDTO;
import com.mss301.documentservice.dto.TextChunkDTO;
import com.mss301.documentservice.dto.request.EmbedRequest;
import com.mss301.documentservice.entity.DocumentMetadata;
import com.mss301.documentservice.exception.DocumentProcessingException;
import com.mss301.documentservice.exception.UnsupportedFormatException;
import com.mss301.documentservice.mapper.DocumentMapper;
import com.mss301.documentservice.repository.DocumentMetadataRepository;
import com.mss301.documentservice.service.DocumentService;
import com.mss301.documentservice.service.VectorServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final VectorServiceClient vectorServiceClient;
    private final DocumentMetadataRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Value("${document.chunk.size:500}")
    private int chunkSize;

    @Value("${document.chunk.overlap:50}")
    private int chunkOverlap;

    public DocumentMetadataDTO processDocument(MultipartFile file, String userId) {
        log.info("Processing document: {} for user: {}", file.getOriginalFilename(), userId);

        try {
            // 1. Parse document
            String content = parseDocument(file);
            log.debug("Document parsed, content length: {}", content.length());

            // 2. Chunk content
            List<TextChunkDTO> chunks = chunkText(content);
            log.info("Document chunked into {} pieces", chunks.size());

            // 3. Create metadata
            DocumentMetadata metadata = DocumentMetadata.builder()
                    .documentId(UUID.randomUUID().toString())
                    .userId(userId)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .chunkCount(chunks.size())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            // 4. Save metadata to database
            documentRepository.save(metadata);
            log.info("Document metadata saved: {}", metadata.getDocumentId());

            // 5. Send to Vector Service for embedding
            try {
                DocumentMetadataDTO metadataDto = documentMapper.toDto(metadata);

                // ✅ FIX: Create proper request object
                EmbedRequest embedRequest = EmbedRequest.builder()
                        .chunks(chunks)
                        .metadata(metadataDto)
                        .build();

                vectorServiceClient.embedAndStore(embedRequest);
                log.info("Chunks sent to Vector Service successfully");
            } catch (Exception e) {
                log.error("Error sending to Vector Service", e);
                // Rollback metadata
                documentRepository.delete(metadata);
                throw new DocumentProcessingException("Failed to process document vectors", e);
            }

            return documentMapper.toDto(metadata);

        } catch (IOException e) {
            log.error("Error processing document", e);
            throw new DocumentProcessingException("Error processing document", e);
        }
    }

    private String parseDocument(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new UnsupportedFormatException("Filename is null");
        }

        fileName = fileName.toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return parsePDF(file.getInputStream());
        } else if (fileName.endsWith(".docx")) {
            return parseWord(file.getInputStream());
        } else if (fileName.endsWith(".txt")) {
            return new String(file.getBytes());
        } else {
            throw new UnsupportedFormatException("Unsupported file format: " + fileName);
        }
    }

    private String parsePDF(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String parseWord(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private List<TextChunkDTO> chunkText(String text) {
        List<TextChunkDTO> chunks = new ArrayList<>();

        // Clean and normalize text
        text = text.replaceAll("\\s+", " ").trim();

        // Split into sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder currentChunk = new StringBuilder();
        int currentWordCount = 0;
        int chunkIndex = 0;

        for (String sentence : sentences) {
            String[] words = sentence.split("\\s+");
            int sentenceWordCount = words.length;

            // If adding this sentence exceeds chunk size, save current chunk
            if (currentWordCount + sentenceWordCount > chunkSize && currentWordCount > 0) {
                chunks.add(TextChunkDTO.builder()
                        .index(chunkIndex++)
                        .text(currentChunk.toString().trim())
                        .wordCount(currentWordCount)
                        .build());

                // Create overlap
                String[] currentWords = currentChunk.toString().split("\\s+");
                currentChunk = new StringBuilder();
                int overlapWords = Math.min(chunkOverlap, currentWords.length);

                for (int i = currentWords.length - overlapWords; i < currentWords.length; i++) {
                    currentChunk.append(currentWords[i]).append(" ");
                }
                currentWordCount = overlapWords;
            }

            currentChunk.append(sentence).append(" ");
            currentWordCount += sentenceWordCount;
        }

        // Add final chunk if exists
        if (currentWordCount > 0) {
            chunks.add(TextChunkDTO.builder()
                    .index(chunkIndex)
                    .text(currentChunk.toString().trim())
                    .wordCount(currentWordCount)
                    .build());
        }

        log.info("Created {} chunks from text", chunks.size());
        return chunks;
    }

    public List<DocumentMetadataDTO> getUserDocuments(String userId) {
        return documentRepository.findByUserId(userId)
                .stream()
                .map(documentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteDocument(String documentId, String userId) {
        DocumentMetadata metadata = documentRepository
                .findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new DocumentProcessingException("Document not found"));

        // Delete from vector DB
        try {
            vectorServiceClient.deleteDocument(documentId);
        } catch (Exception e) {
            log.error("Error deleting from vector service", e);
        }

        // Delete metadata
        documentRepository.delete(metadata);
        log.info("Document deleted: {}", documentId);
    }
}
