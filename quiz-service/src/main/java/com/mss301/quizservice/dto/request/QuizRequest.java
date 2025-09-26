package com.mss301.quizservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizRequest {
        String id;
        String name;
        String description;
        String category;
        String pdfUrl;
        String answerKeyUrl;
        String createdBy;
        Timestamp createdDate;
        Integer duration;
        Double price;
}
