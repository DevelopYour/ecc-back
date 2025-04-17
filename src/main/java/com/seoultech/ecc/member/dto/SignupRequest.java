package com.seoultech.ecc.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "학번은 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{8}$", message = "학번은 8자리 숫자여야 합니다.")
    private String studentId;

    @NotNull(message = "전공은 필수 입력 항목입니다.")
    private Long majorId;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", message = "올바른 형식의 전화번호를 입력해주세요.")
    private String tel;

    @NotBlank(message = "카카오톡 아이디는 필수 입력 항목입니다.")
    private String kakaoTel;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @NotNull(message = "영어 실력은 필수 선택 항목입니다.")
    private Integer level;

    @NotBlank(message = "지원 동기는 필수 입력 항목입니다.")
    private String motivation;
}