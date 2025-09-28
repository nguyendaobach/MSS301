package com.mss301.quizservice.dto.response;

import com.mss301.quizservice.entity.DocumentReview;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentResponse {
    String name;
    String description;
    String category;
    String url;
    Double price;
    String createdBy;
    Timestamp createdDate;
    Integer downloadCount;
    List<DocumentReview> reviews;
}
