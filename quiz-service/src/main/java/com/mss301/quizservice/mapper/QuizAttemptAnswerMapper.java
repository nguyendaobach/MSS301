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

    public List<QuizAttemptAnswerResponse> toResponseList(List<QuizAttemptAnswer> answers) {
        if (answers == null || answers.isEmpty()) return List.of();

        // Lấy QuizAttempt và Quiz từ phần tử đầu tiên
        QuizAttempt attempt = answers.get(0).getQuizAttempt();
        QuizAnswerKey answerKey = answerKeyRepository.findByQuiz(attempt.getQuiz())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_KEY_NOT_FOUND));

        return answers.get(0).getAnswers().entrySet().stream()
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

