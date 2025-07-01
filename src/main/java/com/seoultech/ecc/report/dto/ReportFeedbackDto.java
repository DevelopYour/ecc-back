package com.seoultech.ecc.report.dto;

import com.seoultech.ecc.study.datamodel.ExpressionRedis;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportFeedbackDto {
    private String english;
    private String korean;
    private String original;
    private String feedback;

    public static ReportFeedbackDto fromRedis(ExpressionRedis redis) {
        return ReportFeedbackDto.builder()
                .english(redis.getEnglish())
                .korean(redis.getKorean())
                .original(redis.getOriginal())
                .feedback(redis.getFeedback())
                .build();
    }
}
