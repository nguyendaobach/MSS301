package com.mss301.quizservice.service;

import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.dto.response.QuizResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface QuizService {
    //search and filter
    List<QuizResponse> search(String category, int page, int size);

    // for Quiz
    QuizResponse getQuizById( String quizId);
    QuizResponse createQuizzes(QuizRequest quizRequest,String createdBy, MultipartFile file);
    QuizResponse updateQuizzes(String id, String createdBy,QuizRequest quizRequest);
    void deleteQuiz(String quizId);



    // for Key
    Map<Integer, String> setAnswerKey(String quizId, Map<Integer, String> answers);
    Map<Integer, String> getAnswerKey(String quizId);

    //for attempts
    QuizAttemptResponse startAttempt(String quizId, String userId);

    QuizAttemptResponse submitAttempt(String quizId, String attemptId, Map<Integer, String> answers);

    QuizAttemptResponse getAttemptDetail(String attemptId);

    List<QuizAttemptResponse> getUserAttempts(String userId);





}
