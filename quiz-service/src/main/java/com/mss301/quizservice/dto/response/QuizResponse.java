package com.mss301.quizservice.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponse {
    String name;
    String description;
    String category;
    String pdfUrl;
    String createdBy;
    Timestamp createdDate;
    Integer duration;
    Double price;
}
