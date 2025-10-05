package com.mss301.quizservice.service;

import com.mss301.quizservice.dto.request.DocumentRequest;
import com.mss301.quizservice.dto.request.DocumentReviewRequest;
import com.mss301.quizservice.dto.response.DocumentResponse;
import com.mss301.quizservice.entity.DocumentReview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    List<DocumentResponse> search(String category, int page, int size);
    DocumentResponse createDocument(DocumentRequest request, String createdBy,  MultipartFile file);
    DocumentResponse updateDocument(String id,String createdBy, DocumentRequest request);
    void deleteDocument(String id);
    void incrementDownloadCount(String documentId);
    int getDownloadCount(String documentId);
    void rateDocument(DocumentReviewRequest request);
    List<DocumentReview> getAllReviews(String documentId);
}

