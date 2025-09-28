package com.mss301.quizservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadPdf(MultipartFile file);
}
