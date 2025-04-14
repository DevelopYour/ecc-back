package com.seoultech.ecc.exception;

import com.seoultech.ecc.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(ResponseDto.error("입력값 검증에 실패했습니다.", errors));
    }

    // 리소스를 찾을 수 없는 경우
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDto<Void>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // 메시지 내용에 따라 상태 코드 결정
        if (message != null) {
            if (message.contains("찾을 수 없습니다") || message.contains("존재하지 않는")) {
                status = HttpStatus.NOT_FOUND;
            } else if (message.contains("접근 권한이 없습니다") || message.contains("권한이 부족합니다")) {
                status = HttpStatus.FORBIDDEN;
            } else if (message.contains("인증") || message.contains("로그인") || message.contains("비밀번호")) {
                status = HttpStatus.UNAUTHORIZED;
            }
        }

        return ResponseEntity
                .status(status)
                .body(ResponseDto.error(message));
    }

    // Spring Security 관련 예외 처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ResponseDto.error("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    // 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleAllExceptions(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.error("서버 오류가 발생했습니다."));
    }
}