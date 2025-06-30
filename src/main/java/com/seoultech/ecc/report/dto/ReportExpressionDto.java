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
    private String example;
    private String feedback;
    private boolean translation;

    public static ReportExpressionDto fromRedis(ExpressionRedis redis) {
        return ReportExpressionDto.builder()
                .english(redis.getEnglish())
                .korean(redis.getKorean())
                .example(redis.getExample())
                .feedback(redis.getFeedback())
                .translation(redis.isTranslation())
                .build();
    }
}
