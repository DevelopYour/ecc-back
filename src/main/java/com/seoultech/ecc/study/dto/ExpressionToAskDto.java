package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionToAskDto {
    private Integer topicId;
    private String question;
    private boolean translation;
    private boolean korean;
}
