package com.mss301.quizservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAttemptResponse {
    String quizAttemptId;
    String userId;
    Timestamp startedAt;
    Timestamp completedAt;
    Double score;
    String status; // from enum (IN_PROGRESS, COMPLETED)

    // Nested answer details
    List<QuizAttemptAnswerResponse> answers;
}
