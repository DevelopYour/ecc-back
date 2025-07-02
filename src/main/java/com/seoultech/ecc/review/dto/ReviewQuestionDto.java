package com.seoultech.ecc.review.dto;

import com.seoultech.ecc.report.dto.ReportFeedbackDto;
import com.seoultech.ecc.report.dto.ReportTranslationDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewQuestionDto {
    private String question; // 문제
    private String answer; // 답
    private boolean personal; // 개인 맞춤형 or 팀원 공통 문항 여부
    private ReviewResponseDto response; // 사용자 답변

    public static ReviewQuestionDto fromTranslation(ReportTranslationDto translationDto) {
        return ReviewQuestionDto.builder()
                .question(translationDto.getExampleKorean())
                .answer(translationDto.getExampleEnglish())
                .personal(false)
                .response(null)
                .build();
    }

    public static ReviewQuestionDto fromFeedback(ReportFeedbackDto feedbackDto) {
        return ReviewQuestionDto.builder()
                .question(feedbackDto.getKorean())
                .answer(feedbackDto.getEnglish())
                .personal(false)
                .response(null)
                .build();
    }
}
