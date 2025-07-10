package com.seoultech.ecc.admin.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {
    private int totalTeams;
    private int totalSubmittedReports;
    private int totalExpectedReports;
    private double overallSubmissionRate;
    private double overallAverageGrade;
}