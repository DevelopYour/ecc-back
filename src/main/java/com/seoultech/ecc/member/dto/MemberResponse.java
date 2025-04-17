package com.seoultech.ecc.member.dto;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.member.datamodel.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponse {

    private Integer uuid; // 회원ID
    private String studentId; // 학번 (로그인 ID)로 사용
    private String name;
    private String tel;
    private String kakaoTel; // 카카오톡 아이디
    private String email;
    private Integer level;
    private Double rate;
    private MemberStatus status;
    private Long majorId;
    private String majorName; // 학과명
    private String motivation; // 지원 동기
    private String role; // 역할 정보 추가

    // 카카오 로그인은 현재 미구현
    /*private Integer kakaoUuid; // 카카오로그인ID*/

    public static MemberResponse fromEntity(MemberEntity member) {
        return MemberResponse.builder()
                .uuid(member.getUuid())
                .studentId(member.getStudentId())
                .name(member.getName())
                .tel(member.getTel())
                .kakaoTel(member.getKakaoTel())
                .email(member.getEmail())
                .level(member.getLevel())
                .rate(member.getRate())
                .status(member.getStatus())
                .majorId(member.getMajor().getId())
                .majorName(member.getMajor().getName())
                .motivation(member.getMotivation())
                .role(member.getRole()) // 역할 정보 추가
                .build();
    }
}