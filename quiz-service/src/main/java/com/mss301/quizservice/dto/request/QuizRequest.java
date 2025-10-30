package com.mss301.quizservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizRequest {

    @NotBlank(message = "Quiz name cannot be empty")
    @Size(max = 255, message = "Quiz name cannot exceed 255 characters")
    String name;

    @Size(max = 1000, message = "Description too long (max 1000 characters)")
    String description;

    @NotBlank(message = "Category is required")
    String category;

    @NotNull(message = "Number of questions is required")
    @Min(value = 1, message = "Quiz must have at least 1 question")
    @Max(value = 200, message = "Quiz cannot have more than 200 questions")
    Integer numQuestions;

    Timestamp createdDate;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 150, message = "Duration cannot exceed 150 minutes")
    Integer duration;
}
