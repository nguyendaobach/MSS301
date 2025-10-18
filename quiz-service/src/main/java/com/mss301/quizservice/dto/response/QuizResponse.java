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
    String id;
    String createdBy;
    String name;
    String description;
    Timestamp createdDate;
    String category;
    Integer numQuestions;
    String url;
    Integer duration;
}
