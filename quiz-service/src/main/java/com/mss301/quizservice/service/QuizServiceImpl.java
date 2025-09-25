package com.mss301.quizservice.service;

import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.dto.response.QuizResponse;
import com.mss301.quizservice.entity.Quiz;
import com.mss301.quizservice.exception.AppException;
import com.mss301.quizservice.exception.ErrorCode;
import com.mss301.quizservice.mapper.QuizMapper;
import com.mss301.quizservice.repository.QuizAnswerKeyRepository;
import com.mss301.quizservice.repository.QuizAttemptsAnswerRepository;
import com.mss301.quizservice.repository.QuizAttemptsRepository;
import com.mss301.quizservice.repository.QuizRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuizServiceImpl implements QuizService {
    QuizRepository quizRepository;
    QuizAnswerKeyRepository quizAnswerKeyRepository;
    QuizAttemptsRepository quizAttemptsRepository;
    QuizAttemptsAnswerRepository quizAttemptsAnswerRepository;
    QuizMapper quizMapper;


    @Override
    public List<QuizResponse> getAllQuizzes(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Quiz> quizzesPage;

        if (category != null && !category.isBlank()) {
            quizzesPage = quizRepository.findByCategory(category, pageable);
        } else {
            quizzesPage = quizRepository.findAll(pageable);
        }

        return quizzesPage.getContent().stream()
                .map(quiz -> new QuizResponse(
                        quiz.getName(),
                        quiz.getDescription(),
                        quiz.getCategory(),
                        quiz.getPdfUrl(),
                        quiz.getCreatedBy(),
                        quiz.getCreatedDate(),
                        quiz.getDuration(),
                        quiz.getPrice()
                ))
                .toList();
    }


    @Override
    public QuizResponse getQuizById(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
        }
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() ->  new AppException(ErrorCode.QUIZ_NOT_FOUND));
        return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public QuizResponse createQuizzes(QuizRequest quizRequest) {
       Quiz quiz = quizRepository.save(quizMapper.toQuiz(quizRequest));
       log.info("Created quizzes for quiz with id: " + quizRequest.getId());
       return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public QuizResponse updateQuizzes(QuizRequest quizRequest) {
       if(quizRepository.findById(quizRequest.getId()).isEmpty()) {
           throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
       }
       Quiz quiz = quizRepository.save(quizMapper.toQuiz(quizRequest));
       log.info("Updated quizzes for quiz with id: " + quizRequest.getId());
       return  quizMapper.toQuizResponse(quiz);
    }

    @Override
    public void deleteQuiz(String quizId) {
        if(quizRepository.findById(quizId).isEmpty()) {
            throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
        }
        quizRepository.deleteById(quizId);
        log.info("Deleted quizzes");
    }

    @Override
    public Map<Integer, String> setAnswerKey(String quizId, Map<Integer, String> answers) {
        return Map.of();
    }

    @Override
    public Map<Integer, String> getAnswerKey(String quizId) {
        return Map.of();
    }

    @Override
    public QuizAttemptResponse startAttempt(String quizId, String userId) {
        return null;
    }

    @Override
    public QuizAttemptResponse submitAttempt(String quizId, String attemptId, Map<Integer, String> answers) {
        return null;
    }

    @Override
    public QuizAttemptResponse getAttemptDetail(String attemptId) {
        return null;
    }

    @Override
    public List<QuizAttemptResponse> getUserAttempts(String userId) {
        return List.of();
    }
}
