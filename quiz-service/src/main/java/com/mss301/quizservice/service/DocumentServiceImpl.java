package com.mss301.quizservice.service;

import com.mss301.quizservice.dto.request.DocumentRequest;
import com.mss301.quizservice.dto.request.DocumentReviewRequest;
import com.mss301.quizservice.dto.response.DocumentResponse;
import com.mss301.quizservice.entity.Document;
import com.mss301.quizservice.entity.DocumentReview;
import com.mss301.quizservice.exception.AppException;
import com.mss301.quizservice.exception.ErrorCode;
import com.mss301.quizservice.mapper.DocumentMapper;
import com.mss301.quizservice.repository.DocumentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DocumentServiceImpl implements DocumentService {
    DocumentRepository documentRepository;
    DocumentMapper documentMapper;
    FileService fileService;

    @Override
    public List<DocumentResponse> search(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Document> documentPage;

        if (category != null && !category.isBlank()) {
            documentPage = documentRepository.findByCategory(category, pageable);
        } else {
            documentPage = documentRepository.findAll(pageable);
        }

        return documentPage.getContent().stream()
                .map(document -> new DocumentResponse(
                        document.getName(),
                        document.getDescription(),
                        document.getCategory(),
                        document.getUrl(),
                        document.getPrice(),
                        document.getCreatedBy(),
                        document.getCreatedDate(),
                        document.getDownloadCount(),
                        document.getReviews()
                ))
                .toList();
    }

    @Override
    public DocumentResponse createDocument(DocumentRequest request, MultipartFile file) {
        String pdfUrl = fileService.uploadPdf(file);
        Document document = documentMapper.toDocument(request);
        document.setUrl(pdfUrl);
        document.setDownloadCount(0);
        document.setReviews(new ArrayList<>());
        return documentMapper.toResponse(documentRepository.save(document));
    }

    @Override
    public DocumentResponse updateDocument(String id, DocumentRequest request) {
        Document existing = documentRepository.findById(id).orElseThrow();
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setUrl(request.getUrl());
        existing.setPrice(request.getPrice());
        return documentMapper.toResponse(documentRepository.save(existing));
    }

    @Override
    public void deleteDocument(String id) {
        documentRepository.deleteById(id);
    }


    @Override
    public void incrementDownloadCount(String documentId) {
        Document doc = documentRepository.findById(documentId).orElseThrow();
        doc.setDownloadCount(doc.getDownloadCount() + 1);
        documentRepository.save(doc);
    }

    @Override
    public int getDownloadCount(String documentId) {
        return documentRepository.findById(documentId).get().getDownloadCount();
    }

    @Override
    public void rateDocument(DocumentReviewRequest request) {
        Document doc = documentRepository.findById(request.getDocumentId()).orElseThrow(()->new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        DocumentReview review = new DocumentReview();
        review.setRating(request.getRating());
        review.setReviewer(request.getReviewer());
        review.setComment(request.getComment());
        review.setReviewedAt(new Timestamp(System.currentTimeMillis()));

        doc.getReviews().add(review);
        documentRepository.save(doc);
    }

    @Override
    public List<DocumentReview> getAllReviews(String documentId) {
        if (documentRepository.findById(documentId).isEmpty()) {
            throw  new AppException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        return documentRepository.findById(documentId).get().getReviews();
    }

}
