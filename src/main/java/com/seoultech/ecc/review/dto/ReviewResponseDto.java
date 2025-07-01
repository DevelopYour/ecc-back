package com.seoultech.ecc.review.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private String userAnswer;
    private boolean correct;
}
