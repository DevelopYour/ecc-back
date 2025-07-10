package com.seoultech.ecc.admin.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamReportStatusDto {
    private Integer teamId;
    private String teamName;
    private List<WeeklyStatusDto> weeklyStatus;
    private Integer totalWeeks;
    private Integer submittedReports;
    private double submissionRate;
    private double averageGrade;
}
