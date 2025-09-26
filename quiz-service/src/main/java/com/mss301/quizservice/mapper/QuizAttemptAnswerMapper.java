package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.response.QuizAttemptAnswerResponse;
import com.mss301.quizservice.entity.QuizAnswerKey;
import com.mss301.quizservice.entity.QuizAttempt;
import com.mss301.quizservice.entity.QuizAttemptAnswer;
import com.mss301.quizservice.exception.AppException;
import com.mss301.quizservice.exception.ErrorCode;
import com.mss301.quizservice.repository.QuizAnswerKeyRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class QuizAttemptAnswerMapper {

    @Autowired
    protected QuizAnswerKeyRepository answerKeyRepository;

    // Map từ QuizAttempt → List<QuizAttemptAnswerResponse>
    public List<QuizAttemptAnswerResponse> toResponseList(QuizAttempt attempt) {
        if (attempt.getAnswers() == null || attempt.getAnswers().isEmpty()) return List.of();

        QuizAnswerKey answerKey = answerKeyRepository.findByQuiz(attempt.getQuiz())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_KEY_NOT_FOUND));

        QuizAttemptAnswer attemptAnswer = attempt.getAnswers().get(0);

        return attemptAnswer.getAnswers().entrySet().stream()
                .map(entry -> {
                    Integer qId = entry.getKey();
                    String selected = entry.getValue();
                    String correct = answerKey.getAnswers().get(qId);
                    boolean isCorrect = correct != null && correct.equals(selected);

                    return QuizAttemptAnswerResponse.builder()
                            .questionId(String.valueOf(qId))
                            .selectedOption(selected)
                            .answer(correct)
                            .isCorrect(isCorrect)
                            .build();
                })
                .toList();
    }
}

