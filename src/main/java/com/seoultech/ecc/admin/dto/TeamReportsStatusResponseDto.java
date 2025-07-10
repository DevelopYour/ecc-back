package com.seoultech.ecc.admin.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamReportsStatusResponseDto {
    private List<TeamReportStatusDto> teamReportStatus;
    private ReportSummaryDto summary;
}
