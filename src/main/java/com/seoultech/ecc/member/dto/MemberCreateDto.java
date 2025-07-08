package com.seoultech.ecc.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCreateDto {
    private String studentId;   // 학번
    // private Integer kakaoUuid;   // 카카오 로그인ID (보류)
    private String tel;
    private String kakaoTel;     // 카카오톡 아이디
    private String name;
    private String email;
    private Integer level;
    private Integer majorId;
}