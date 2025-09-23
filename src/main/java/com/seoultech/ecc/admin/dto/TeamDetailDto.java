package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.team.dto.TeamDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDetailDto {
    private TeamDto team;
    private List<ReportSummaryDto> reports;
}
