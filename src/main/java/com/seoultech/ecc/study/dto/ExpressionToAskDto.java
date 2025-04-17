package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionToAskDto {
    public Long topicId;
    public String question;
}
