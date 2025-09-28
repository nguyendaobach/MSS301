package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.request.DocumentRequest;
import com.mss301.quizservice.dto.response.DocumentResponse;
import com.mss301.quizservice.entity.Document;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document toDocument(DocumentRequest request);
    DocumentResponse toResponse(Document document);
}
