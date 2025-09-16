package com.seoultech.ecc.report.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CorrectionDto {
    private String question; // 문제
    private String answer; // 정답
    private String description; // 설명
}
