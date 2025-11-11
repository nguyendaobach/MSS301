package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.entity.QuizAttempt;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", uses = {QuizAttemptAnswerMapper.class})
public abstract class QuizAttemptMapper {

    @Autowired
    protected QuizAttemptAnswerMapper answerMapper;

    // ⚡ Cho /attempts/{id} (có answers)
    @Mapping(source = "quizAttemptId", target = "quizAttemptId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "startedAt", target = "startedAt")
    @Mapping(source = "completedAt", target = "completedAt")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "quizId", ignore = true) // sẽ set thủ công sau
    @Mapping(target = "answers", expression = "java(answerMapper.toResponseList(quizAttempt.getAnswers()))")
    public abstract QuizAttemptResponse toResponse(QuizAttempt quizAttempt);

    @Mapping(source = "quizAttemptId", target = "quizAttemptId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "quizId", ignore = true)
    @Mapping(target = "answers", ignore = true)
    public abstract QuizAttemptResponse toResponseWithoutAnswers(QuizAttempt quizAttempt);

    @AfterMapping
    protected void mapQuizId(@MappingTarget QuizAttemptResponse response, QuizAttempt attempt) {
        if (attempt.getQuiz() != null) {
            response.setQuizId(attempt.getQuiz().getId());
        }
    }
}


