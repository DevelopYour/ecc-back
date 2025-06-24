package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionToAskDto {
    public Integer topicId;
    public String question;
}
