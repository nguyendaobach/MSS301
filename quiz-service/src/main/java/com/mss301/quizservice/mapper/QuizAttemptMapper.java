package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.entity.QuizAttempt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizAttemptMapper {
    QuizAttemptResponse toResponse(QuizAttempt quizAttempt);
}
