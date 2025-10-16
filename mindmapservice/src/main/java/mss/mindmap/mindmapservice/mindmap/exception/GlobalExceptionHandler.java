package mss.mindmap.mindmapservice.mindmap.exception;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import mss.mindmap.mindmapservice.mindmap.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.AppendableJoiner.builder;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
        log.error("Exception: ", exception);
        ApiResponse<?> apiResponse  =  ApiResponse.builder()
                .message(exception.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> apiResponse =  ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    ResponseEntity<ApiResponse<?>> EntityNotFoundException(EntityNotFoundException exception) {

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(exception.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .success(false)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(apiResponse);
    }


}
