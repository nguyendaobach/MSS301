package com.mss301.quizservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizAttemptAnswerResponse {
    String questionId;
    String selectedOption;
    Boolean isCorrect;
}
