package com.seoultech.ecc.review.dto;

import com.seoultech.ecc.review.datamodel.ReviewTestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewTestResponseDto {

    private String id; // 기존 MongoDB의 String id 호환성 유지
    private Integer userId;
    private List<QuestionDto> questions;
    private boolean isComplete;

    public static ReviewTestResponseDto fromEntity(ReviewTestEntity entity) {
        List<QuestionDto> questions = entity.getQuestions().stream()
                .sorted((q1, q2) -> Integer.compare(q1.getQuestionOrder(), q2.getQuestionOrder()))
                .map(q -> QuestionDto.builder()
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .isCorrect(q.isCorrect())
                        .build())
                .collect(Collectors.toList());

        return ReviewTestResponseDto.builder()
                .id(entity.getId().toString()) // Integer를 String으로 변환하여 호환성 유지
                .userId(entity.getUserId())
                .questions(questions)
                .isComplete(entity.isComplete())
                .build();
    }
}