package com.seoultech.ecc.report.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDto {
    private Long reportId;
    private int week;
    private String contents;
    private int grade;
    private Long teamId;
    private Long subjectId;
}
