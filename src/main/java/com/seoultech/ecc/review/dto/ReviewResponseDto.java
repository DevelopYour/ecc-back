package com.seoultech.ecc.review.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private String response;
    private boolean correct;
}
