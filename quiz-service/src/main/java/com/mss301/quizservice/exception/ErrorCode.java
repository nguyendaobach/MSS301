package com.mss301.quizservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    QUIZ_NOT_FOUND(1002, "Quiz not found", HttpStatus.NOT_FOUND),
    QUIZ_ATTEMPT_NOT_FOUND(1003, "Quiz attempt not found", HttpStatus.NOT_FOUND),
    ANSWER_KEY_NOT_FOUND(1004, "Answer key not found", HttpStatus.NOT_FOUND),
    ATTEMPT_NOT_FOUND(1005, "Attempt not found", HttpStatus.NOT_FOUND),
    INVALID_QUIZ_ATTEMPT(1006, "Invalid quiz attempt", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
