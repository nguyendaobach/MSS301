package com.mss301.quizservice.controller;

import com.mss301.quizservice.dto.ApiResponse;
import com.mss301.quizservice.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController{
    FileService fileService;

// ------------------- UPLOAD PDF -------------------
@Operation(summary = "Upload PDF for a quiz")
@PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ApiResponse<String> uploadQuizPdf(
        @Parameter(description = "PDF file to upload")
        @RequestParam("file") MultipartFile file) {
    return ApiResponse.<String>builder()
            .result(fileService.uploadPdf(file))
            .message("PDF uploaded successfully")
            .build();
}
}
