package com.seoultech.ecc.review.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private String question;
    private String answer;
    private boolean isCorrect;
}
