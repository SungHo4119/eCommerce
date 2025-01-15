package com.hhplush.eCommerce;


import com.hhplush.eCommerce.common.exception.ErrorResponse;
import com.hhplush.eCommerce.common.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.common.exception.custom.BadRequestException;
import com.hhplush.eCommerce.common.exception.custom.InvalidPaymentCancellationException;
import com.hhplush.eCommerce.common.exception.custom.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST), e.getMessage()));
    }

    // 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
        ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
            .findFirst() // 첫 번째 항목 가져오기
            .map(violation -> violation.getMessage()).orElse("Invalid Request");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST), message));
    }

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e) {
        // FieldError 목록 추출
        String errorMessages = e.getBindingResult().getAllErrors().stream()
            .findFirst()
            .map(error -> error.getDefaultMessage()).orElse("Invalid Request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST), errorMessages));
    }

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND), e.getMessage()));
    }

    // 409
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(String.valueOf(HttpStatus.CONFLICT), e.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentCancellationException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
        InvalidPaymentCancellationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(String.valueOf(HttpStatus.CONFLICT), e.getMessage()));
    }


    // 500
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), "에러가 발생했습니다."));
    }
}