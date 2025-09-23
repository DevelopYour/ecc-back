package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import lombok.*;

import java.util.List;

/**
 * 팀 멤버 추가/삭제 작업 결과 DTO
 * addTeamMember, removeTeamMember API 전용 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberOperationResultDto {
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
     * 현재 팀 멤버 리스트 (작업 후 상태)
     */
    private List<MemberSimpleDto> members;
}
