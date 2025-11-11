package com.mss301.quizservice.controller;

import com.mss301.quizservice.dto.ApiResponse;
import com.mss301.quizservice.dto.request.QuizRequest;
import com.mss301.quizservice.dto.response.QuizAttemptResponse;
import com.mss301.quizservice.dto.response.QuizResponse;
import com.mss301.quizservice.service.QuizService;
import com.mss301.quizservice.util.HeaderExtractor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;

    // ------------------- QUIZ CRUD -------------------
    @GetMapping
    public ApiResponse<List<QuizResponse>> searchQuiz(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<List<QuizResponse>>builder()
                .result(quizService.search(category, page, size))
                .message("Get quizzes successfully")
                .build();
    }

    @GetMapping("/{quizId}")
    public ApiResponse<QuizResponse> getQuizById(@PathVariable String quizId) {
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.getQuizById(quizId))
                .message("Get quiz detail successfully")
                .build();
    }

    @Operation(summary = "Upload PDF for a quiz")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<QuizResponse> createQuiz(@RequestPart("quizRequest")  @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                                    @Valid QuizRequest quizRequest,
                                                @Parameter(description = "PDF file to upload")
                                                @RequestPart("file") MultipartFile file,
                                                HttpServletRequest httpRequest) {
        String userId = HeaderExtractor.getUserId(httpRequest);

        return ApiResponse.<QuizResponse>builder()
                .result(quizService.createQuizzes(quizRequest, userId,file))
                .message("Quiz created successfully")
                .build();
    }

    @PutMapping("/{quizId}")
    public ApiResponse<QuizResponse> updateQuiz(@RequestBody @Valid QuizRequest quizRequest, @PathVariable String quizId, HttpServletRequest httpRequest) {
        String userId = HeaderExtractor.getUserId(httpRequest);
        return ApiResponse.<QuizResponse>builder()
                .result(quizService.updateQuizzes(quizId, userId, quizRequest))
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
           HttpServletRequest httpServletRequest ) {
        String userId = HeaderExtractor.getUserId(httpServletRequest);
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
        QuizAttemptResponse detail = quizService.getAttemptDetail(attemptId);

        return ApiResponse.<QuizAttemptResponse>builder()
                .result(quizService.getAttemptDetail(attemptId))
                .message("Get attempt detail successfully")
                .result(detail)
                .build();
    }

    @GetMapping("/attempts/history")
    public ApiResponse<List<QuizAttemptResponse>> getUserAttempts(HttpServletRequest httpServletRequest) {
        String userId = HeaderExtractor.getUserId(httpServletRequest);
        return ApiResponse.<List<QuizAttemptResponse>>builder()
                .result(quizService.getUserAttempts(userId))
                .message("Get user attempts successfully")
                .build();
    }

}
