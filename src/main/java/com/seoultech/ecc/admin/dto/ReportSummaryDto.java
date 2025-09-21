package com.seoultech.ecc.admin.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryDto {
    private String id;
    private int week;
    private LocalDateTime submittedAt;
}
