package com.seoultech.ecc.study.dto;

import com.seoultech.ecc.study.datamodel.ReviewStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto {
    private String reviewId;
    private Integer memberId; // uuid
    private String memberName;
    private ReviewStatus reviewStatus;
}
