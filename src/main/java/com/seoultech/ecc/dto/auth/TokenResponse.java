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
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long accessTokenExpiresIn;
    private Integer uuid;
    private String studentId;
    private String name;
    private MemberStatus status;
    private String role;
}