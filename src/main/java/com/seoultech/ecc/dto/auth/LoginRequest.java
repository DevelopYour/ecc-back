package com.seoultech.ecc.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "학번은 필수 입력 항목입니다.")
    private String studentId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}