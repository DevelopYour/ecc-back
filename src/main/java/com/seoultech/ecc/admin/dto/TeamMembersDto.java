package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import lombok.*;

import java.util.List;

/**
 * 팀 멤버 조회 응답 DTO
 * getTeamMembers API 전용 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMembersDto {
    /**
     * 팀 ID
     */
    private Integer teamId;

    /**
     * 팀 이름
     */
    private String teamName;

    /**
     * 정규 스터디 여부
     */
    private Boolean isRegular;

    /**
     * 팀 멤버 리스트
     */
    private List<MemberSimpleDto> members;
}
