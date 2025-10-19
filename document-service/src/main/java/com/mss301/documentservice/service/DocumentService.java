package com.mss301.documentservice.service;

import com.mss301.documentservice.dto.DocumentMetadataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentMetadataDTO processDocument(MultipartFile file, String userId);
    List<DocumentMetadataDTO> getUserDocuments(String userId);
    void deleteDocument(String documentId, String userId);
}
