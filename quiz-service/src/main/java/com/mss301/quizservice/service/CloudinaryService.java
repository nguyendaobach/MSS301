package com.mss301.quizservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            String resourceType = "auto";

            if (contentType != null && contentType.equals("application/pdf")) {
                resourceType = "raw"; // PDF cần upload kiểu raw
            }

            // ✅ Giữ nguyên tên gốc có đuôi .pdf
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                originalFilename = "file.pdf";
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", resourceType,
                            // 👇 Giữ tên có đuôi .pdf để Cloudinary lưu đúng loại file
                            "public_id", originalFilename
                    )
            );

            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload file to Cloudinary failed", e);
        }
    }
}
