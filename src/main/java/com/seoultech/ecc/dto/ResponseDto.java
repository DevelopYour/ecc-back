package com.seoultech.ecc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();

    }

    public static <T> ResponseDto<T> error(String message) {
        return ResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResponseDto<T> error(String message, T data) {
        return ResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
