package com.seoultech.ecc.dto.auth;

import com.seoultech.ecc.entity.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;       // 로그인 성공 시 발급되는 인증 토큰 (향후 JWT 사용 시)
    private Integer uuid;       // 회원 ID
    private String studentId;   // 학번
    private String name;        // 회원 이름
    private MemberStatus status;      // 회원 상태
}