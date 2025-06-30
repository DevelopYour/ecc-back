package com.seoultech.ecc.report.dto;

import com.seoultech.ecc.study.datamodel.ExpressionRedis;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportExpressionDto {
    private String english;
    private String korean;
    private String example; // 번역 only
    private String original; // 교정 only
    private String feedback; // 교정 only
    private boolean translation;

    public static ReportExpressionDto fromRedis(ExpressionRedis redis) {
        return ReportExpressionDto.builder()
                .english(redis.getEnglish())
                .korean(redis.getKorean())
                .example(redis.getExample())
                .original(redis.getOriginal())
                .feedback(redis.getFeedback())
                .translation(redis.isTranslation())
                .build();
    }
}
