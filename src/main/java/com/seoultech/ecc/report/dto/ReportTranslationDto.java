package com.seoultech.ecc.report.dto;

import com.seoultech.ecc.study.datamodel.ExpressionRedis;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportTranslationDto {
    private String english;
    private String korean;
    private String example;

    public static ReportTranslationDto fromRedis(ExpressionRedis redis) {
        return ReportTranslationDto.builder()
                .english(redis.getEnglish())
                .korean(redis.getKorean())
                .example(redis.getExample())
                .build();
    }
}
