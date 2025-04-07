package com.seoultech.ecc.dto;

import com.seoultech.ecc.entity.MemberStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
    private Integer uuid;        // 회원 ID (학번)
    private String stduentId;   // 학번
    //    private Integer kakaoUuid;   // 카카오 로그인ID
    private String password;
    private String tel;
    private String kakaoTel;     // 카카오 아이디
    private String name;
    private String email;
    private Integer level;
    private Double rate;
    private MemberStatus status;
    private Long majorId;
    private String majorName;
}