package com.seoultech.ecc.ai.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExpressionResponse {
    private String korean;
    private String english;
    private String feedback;  // 교정 only) 피드백
    private String exampleEnglish;   // 번역 only) 예문
    private String exampleKorean;   // 번역 only) 예문
}
