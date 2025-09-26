package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.entity.QuizAttempt;
import com.mss301.quizservice.entity.QuizAttemptAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", uses = {QuizAttemptAnswerMapper.class})
public abstract class QuizAttemptMapper {

    @Autowired
    protected QuizAttemptAnswerMapper answerMapper;

    @Mapping(source = "quizAttemptId", target = "quizAttemptId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "startedAt", target = "startedAt")
    @Mapping(source = "completedAt", target = "completedAt")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "answers", expression = "java(answerMapper.toResponseList(quizAttempt))")
    public abstract QuizAttemptResponse toResponse(QuizAttempt quizAttempt);
}
