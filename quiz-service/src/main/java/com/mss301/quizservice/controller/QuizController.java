package com.mss301.quizservice.controller;

import com.mss301.quizservice.dto.ApiResponse;
import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.dto.response.QuizResponse;
import com.mss301.quizservice.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // ------------------- QUIZ CRUD -------------------
    @GetMapping
    public ApiResponse<List<QuizResponse>> getAllQuizzes(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<List<QuizResponse>>builder()
                .result(quizService.getAllQuizzes(category, page, size))
                .message("Get all quizzes successfully")
                .build();
    }

    @GetMapping("/{quizId}")
    public ApiResponse<QuizResponse> getQuizById(@PathVariable String quizId) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.getQuizById(quizId))
                .message("Get quiz detail successfully")
                .build();
    }

    @PostMapping
    public ApiResponse<QuizResponse> createQuiz(@RequestBody QuizRequest quizRequest) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.createQuizzes(quizRequest))
                .message("Quiz created successfully")
                .build();
    }

    @PutMapping("/{quizId}")
    public ApiResponse<QuizResponse> updateQuiz(@PathVariable String quizId,
                                                @RequestBody QuizRequest quizRequest) {
        quizRequest.setId(quizId);
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.updateQuizzes(quizRequest))
                .message("Quiz updated successfully")
                .build();
    }

    @DeleteMapping("/{quizId}")
    public ApiResponse<Void> deleteQuiz(@PathVariable String quizId) {
        quizService.deleteQuiz(quizId);
        return ApiResponse.<Void>builder()
                .message("Quiz deleted successfully")
                .build();
    }

    // ------------------- ANSWER KEY -------------------
    @PostMapping("/{quizId}/answer-key")
    public ApiResponse<Map<Integer, String>> setAnswerKey(
            @PathVariable String quizId,
            @RequestBody Map<Integer, String> answers) {
        return ApiResponse.<Map<Integer, String>>builder()
                .result(quizService.setAnswerKey(quizId, answers))
                .message("Answer key set successfully")
                .build();
    }

    @GetMapping("/{quizId}/answer-key")
    public ApiResponse<Map<Integer, String>> getAnswerKey(@PathVariable String quizId) {
        return ApiResponse.<Map<Integer, String>>builder()
                .result(quizService.getAnswerKey(quizId))
                .message("Get answer key successfully")
                .build();
    }

    // ------------------- ATTEMPTS -------------------
    @PostMapping("/{quizId}/attempts")
    public ApiResponse<QuizAttemptResponse> startAttempt(
            @PathVariable String quizId,
            @RequestParam String userId) {
        return ApiResponse.<QuizAttemptResponse>builder()
                .result(quizService.startAttempt(quizId, userId))
                .message("Attempt started successfully")
                .build();
    }

    @PostMapping("/{quizId}/attempts/{attemptId}/submit")
    public ApiResponse<QuizAttemptResponse> submitAttempt(
            @PathVariable String quizId,
            @PathVariable String attemptId,
            @RequestBody Map<Integer, String> answers) {
        return ApiResponse.<QuizAttemptResponse>builder()
                .result(quizService.submitAttempt(quizId, attemptId, answers))
                .message("Attempt submitted successfully")
                .build();
    }

    @GetMapping("/attempts/{attemptId}")
    public ApiResponse<QuizAttemptResponse> getAttemptDetail(@PathVariable String attemptId) {
        return ApiResponse.<QuizAttemptResponse>builder()
                .result(quizService.getAttemptDetail(attemptId))
                .message("Get attempt detail successfully")
                .build();
    }

    @GetMapping("/user/{userId}/attempts")
    public ApiResponse<List<QuizAttemptResponse>> getUserAttempts(@PathVariable String userId) {
        return ApiResponse.<List<QuizAttemptResponse>>builder()
                .result(quizService.getUserAttempts(userId))
                .message("Get user attempts successfully")
                .build();
    }

    // ------------------- UPLOAD PDF -------------------
    @PostMapping("/{quizId}/upload")
    public ApiResponse<String> uploadQuizPdf(
            @PathVariable String quizId,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.<String>builder()
                .result(quizService.uploadQuizPdf(quizId, file))
                .message("PDF uploaded successfully")
                .build();
    }
}
