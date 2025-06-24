package com.seoultech.ecc.report.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDto {
    private Integer reportId;
    private int week;
    private String contents;
    private int grade;
    private Integer teamId;
    private Integer subjectId;
}
