package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.ReviewStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto {
    private Long reviewId;
    private String memberId; // 학번
    private String memberName;
    private ReviewStatus reviewStatus;
}
