package com.mss301.quizservice.controller;

import com.mss301.quizservice.dto.ApiResponse;
import com.mss301.quizservice.dto.request.DocumentRequest;
import com.mss301.quizservice.dto.request.DocumentReviewRequest;
import com.mss301.quizservice.dto.response.DocumentResponse;
import com.mss301.quizservice.entity.DocumentReview;
import com.mss301.quizservice.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @Operation(summary = "Search documents by category with pagination")
    @GetMapping
    public ApiResponse<List<DocumentResponse>> searchDocuments(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<List<DocumentResponse>>builder()
                .result(documentService.search(category, page, size))
                .message("Documents fetched successfully")
                .build();
    }

    @Operation(summary = "Create a new document with PDF upload")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<DocumentResponse> createDocument(
            @RequestPart("document")  @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            DocumentRequest request,
            @Parameter(description = "PDF file to upload")
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.<DocumentResponse>builder()
                .result(documentService.createDocument(request, file))
                .message("Document created successfully")
                .build();
    }

    @Operation(summary = "Update a document")
    @PutMapping("/{id}")
    public ApiResponse<DocumentResponse> updateDocument(
            @PathVariable String id,
            @RequestBody DocumentRequest request
    ) {
        return ApiResponse.<DocumentResponse>builder()
                .result(documentService.updateDocument(id, request))
                .message("Document updated successfully")
                .build();
    }

    @Operation(summary = "Delete a document")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        return ApiResponse.<Void>builder()
                .message("Document deleted successfully")
                .build();
    }

    @Operation(summary = "Increment document download count")
    @PostMapping("/{id}/download")
    public ApiResponse<Void> incrementDownload(@PathVariable String id) {
        documentService.incrementDownloadCount(id);
        return ApiResponse.<Void>builder()
                .message("Download count incremented")
                .build();
    }

    @Operation(summary = "Get download count of a document")
    @GetMapping("/{id}/download-count")
    public ApiResponse<Integer> getDownloadCount(@PathVariable String id) {
        return ApiResponse.<Integer>builder()
                .result(documentService.getDownloadCount(id))
                .message("Download count retrieved successfully")
                .build();
    }

    @Operation(summary = "Rate a document")
    @PostMapping("/{id}/rate")
    public ApiResponse<Void> rateDocument(
            @RequestBody DocumentReviewRequest request
    ) {
        documentService.rateDocument(request);
        return ApiResponse.<Void>builder()
                .message("Document rated successfully")
                .build();
    }

    @Operation(summary = "Get all reviews of a document")
    @GetMapping("/{id}/reviews")
    public ApiResponse<List<DocumentReview>> getAllReviews(@PathVariable String id) {
        return ApiResponse.<List<DocumentReview>>builder()
                .result(documentService.getAllReviews(id))
                .message("Reviews fetched successfully")
                .build();
    }
}
