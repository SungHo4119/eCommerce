package com.hhplush.eCommerce;


import com.hhplush.eCommerce.domain.exception.ErrorResponse;
import com.hhplush.eCommerce.domain.exception.custom.AlreadyExistsException;
import com.hhplush.eCommerce.domain.exception.custom.BadRequestException;
import com.hhplush.eCommerce.domain.exception.custom.ConflictException;
import com.hhplush.eCommerce.domain.exception.custom.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(String.valueOf(HttpStatus.BAD_REQUEST), e.getMessage()));
    }
    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND), e.getMessage()));
    }

    // 409
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(String.valueOf(HttpStatus.CONFLICT), e.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(String.valueOf(HttpStatus.CONFLICT), e.getMessage()));
    }

    // 500
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), "에러가 발생했습니다."));
    }
}