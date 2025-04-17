package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.review.dto.ReviewSummaryDto;
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
