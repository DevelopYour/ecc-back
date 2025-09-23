package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import com.seoultech.ecc.team.dto.TeamDto;
import lombok.*;

import java.util.List;

/**
 * 팀 주차별 상세 정보 DTO
 * getTeamWeekDetail API 전용 응답 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamWeekDetailDto {
    /**
     * 팀 기본 정보
     */
    private TeamDto team;

    /**
     * 해당 주차 보고서
     */
    private ReportDocument report;

    /**
     * 주차별 복습 상태 정보 리스트
     */
    private List<ReviewSummaryDto> reviews;
}
