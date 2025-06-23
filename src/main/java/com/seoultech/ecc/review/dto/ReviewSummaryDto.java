package com.seoultech.ecc.review.dto;

import com.seoultech.ecc.review.datamodel.ReviewStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto {
    private Integer reviewId;
    private Integer memberId; // uuid
    private String memberName;
    private ReviewStatus reviewStatus;
}
