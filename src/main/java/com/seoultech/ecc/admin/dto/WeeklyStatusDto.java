package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyStatusDto {
    private Integer week;
    private boolean submitted;
    private Integer grade;
    private List<ReviewSummaryDto> memberReviews;
}
