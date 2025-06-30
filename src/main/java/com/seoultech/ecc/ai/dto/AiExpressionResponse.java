package com.seoultech.ecc.ai.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExpressionResponse {
    private String korean;
    private String english;
    private String feedback;  // 교정 요청시 피드백
    private String example;   // 번역 요청시 예문
}
