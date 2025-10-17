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
    String name;
    String description;
    String category;
    Integer numQuestions;
    Timestamp createdDate;
    Integer duration;
}
