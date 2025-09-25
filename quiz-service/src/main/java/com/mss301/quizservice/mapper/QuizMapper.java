package com.mss301.quizservice.mapper;

import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizResponse;
import com.mss301.quizservice.entity.Quiz;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    QuizResponse toQuizResponse(Quiz quiz);
    Quiz toQuiz(QuizRequest quizRequest);
}
