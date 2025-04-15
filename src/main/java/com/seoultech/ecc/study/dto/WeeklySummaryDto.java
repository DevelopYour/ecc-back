package com.seoultech.ecc.study.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySummaryDto {
    private StudySummaryDto studySummary;
    private List<ReviewSummaryDto> reviewSummaries;
}
