package com.mss301.quizservice.service;

import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.dto.response.QuizResponse;
import com.mss301.quizservice.entity.Quiz;
import com.mss301.quizservice.entity.QuizAnswerKey;
import com.mss301.quizservice.entity.QuizAttempt;
import com.mss301.quizservice.entity.QuizAttemptAnswer;
import com.mss301.quizservice.exception.AppException;
import com.mss301.quizservice.exception.ErrorCode;
import com.mss301.quizservice.mapper.QuizAttemptMapper;
import com.mss301.quizservice.mapper.QuizMapper;
import com.mss301.quizservice.repository.QuizAnswerKeyRepository;
import com.mss301.quizservice.repository.QuizAttemptAnswerRepository;
import com.mss301.quizservice.repository.QuizAttemptRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuizServiceImpl implements QuizService {
    QuizRepository quizRepository;
    QuizAnswerKeyRepository quizAnswerKeyRepository;
    QuizAttemptRepository quizAttemptRepository;
    QuizAttemptAnswerRepository quizAttemptAnswerRepository;
    QuizMapper quizMapper;
    FileService fileService;
    QuizAttemptMapper quizAttemptMapper;


    @Override
    public List<QuizResponse> search(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Quiz> quizzesPage;

        if (category != null && !category.isBlank()) {
            quizzesPage = quizRepository.findByCategory(category, pageable);
        } else {
            quizzesPage = quizRepository.findAll(pageable);
        }
        log.info("Found {} quizzes", quizzesPage.getTotalElements());

        return quizzesPage.getContent().stream()
                .map(quiz -> new QuizResponse(
                        quiz.getName(),
                        quiz.getDescription(),
                        quiz.getCategory(),
                        quiz.getUrl(),
                        quiz.getDuration()
                ))
                .toList();
    }


    @Override
    public QuizResponse getQuizById(String quizId) {
        if (quizId == null || quizId.isBlank()) {
            throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
        }
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));
        return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public QuizResponse createQuizzes(QuizRequest quizRequest, MultipartFile file) {
        String pdfUrl = fileService.uploadPdf(file);

        Quiz quiz = quizMapper.toQuiz(quizRequest);
        quiz.setUrl(pdfUrl);
        quiz = quizRepository.save(quiz);

        log.info("Created quiz with id: {}", quiz.getId());

        return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public QuizResponse updateQuizzes(QuizRequest quizRequest) {
        if (quizRepository.findById(quizRequest.getId()).isEmpty()) {
            throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
        }
        Quiz quiz = quizRepository.save(quizMapper.toQuiz(quizRequest));
        log.info("Updated quizzes for quiz with id: " + quizRequest.getId());
        return quizMapper.toQuizResponse(quiz);
    }

    @Override
    public void deleteQuiz(String quizId) {
        if (quizRepository.findById(quizId).isEmpty()) {
            throw new AppException(ErrorCode.QUIZ_NOT_FOUND);
        }
        quizRepository.deleteById(quizId);
        log.info("Deleted quizzes");
    }

    @Override
    public Map<Integer, String> setAnswerKey(String quizId, Map<Integer, String> answers) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

        QuizAnswerKey quizAnswerKey = quizAnswerKeyRepository.findByQuiz(quiz)
                .orElse(new QuizAnswerKey());

        quizAnswerKey.setQuiz(quiz);
        quizAnswerKey.setAnswers(answers);

        quizAnswerKeyRepository.save(quizAnswerKey);

        return quizAnswerKey.getAnswers();
    }

    @Override
    public Map<Integer, String> getAnswerKey(String quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

        QuizAnswerKey quizAnswerKey = quizAnswerKeyRepository.findByQuiz(quiz)
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_KEY_NOT_FOUND));

        return quizAnswerKey.getAnswers();
    }


    @Override
    public QuizAttemptResponse startAttempt(String quizId, String userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.QUIZ_NOT_FOUND));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUserId(userId);
        attempt.setStatus(QuizAttempt.Status.IN_PROGRESS);
        attempt.setStartedAt(Timestamp.valueOf(LocalDateTime.now()));

        QuizAttempt saved = quizAttemptRepository.save(attempt);

        return quizAttemptMapper.toResponse(saved);
    }


    @Override
    public QuizAttemptResponse submitAttempt(String quizId, String attemptId, Map<Integer, String> answers) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTEMPT_NOT_FOUND));

        if (!attempt.getQuiz().getId().equals(quizId)) {
            throw new AppException(ErrorCode.INVALID_QUIZ_ATTEMPT);
        }

        QuizAttemptAnswer quizAttemptAnswer = quizAttemptAnswerRepository.findById(attemptId)
                .orElse(new QuizAttemptAnswer(null, attempt, answers));
        quizAttemptAnswerRepository.save(quizAttemptAnswer);
        attempt.setCompletedAt(Timestamp.valueOf(LocalDateTime.now()));
        attempt.setStatus(QuizAttempt.Status.COMPLETED);

        QuizAnswerKey answerKey = quizAnswerKeyRepository.findByQuiz(attempt.getQuiz())
                .orElseThrow(() -> new AppException(ErrorCode.ANSWER_KEY_NOT_FOUND));

            int correctCount = 0;
            for (Map.Entry<Integer, String> entry : answers.entrySet()) {
                String correct = answerKey.getAnswers().get(entry.getKey());
                if (correct != null && correct.equals(entry.getValue())) {
                    correctCount++;
                }
            }

            int totalQuestions = answerKey.getAnswers().size();
            double score = 0.0;
            if (totalQuestions > 0) {
                score = ((double) correctCount / totalQuestions) * 10.0;
            }

            attempt.setScore(score);

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        return quizAttemptMapper.toResponse(saved);
    }


    @Override
    public QuizAttemptResponse getAttemptDetail(String attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTEMPT_NOT_FOUND));

        return quizAttemptMapper.toResponse(attempt);
    }


    @Override
    public List<QuizAttemptResponse> getUserAttempts(String userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        return attempts.stream()
                .map(quizAttemptMapper::toResponse)
                .toList();
    }

}
