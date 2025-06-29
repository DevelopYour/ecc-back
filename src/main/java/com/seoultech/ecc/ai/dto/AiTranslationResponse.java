package com.seoultech.ecc.ai.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTranslationResponse {
    private String korean;
    private String english;
    private String example;
}
