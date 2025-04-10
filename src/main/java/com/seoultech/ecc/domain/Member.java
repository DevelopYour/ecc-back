package com.seoultech.ecc.domain;

import com.seoultech.ecc.entity.MemberStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Member {
    private Integer uuid;        // 회원 ID (학번)
    private String studentId;   // 학번
    // private Integer kakaoUuid;   // 카카오 로그인ID (보류)
    private String password;
    private String tel;
    private String kakaoId;     // 카카오톡 아이디
    private String name;
    private String email;
    private Integer level;
    private Double rate;
    private MemberStatus status;
    private Long majorId;
    private String majorName;
    private String role;
}