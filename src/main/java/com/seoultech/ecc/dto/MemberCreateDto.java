package com.seoultech.ecc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberCreateDto {
    private String studentId;   // 학번
    //    private Integer kakaoUuid;   // 카카오 로그인ID
    private String tel;
    private String kakaoTel;     // 카카오 아이디
    private String name;
    private String email;
    private Integer level;
    private Long majorId;
}