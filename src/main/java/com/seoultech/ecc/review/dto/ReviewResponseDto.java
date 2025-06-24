package com.seoultech.ecc.review.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.review.datamodel.ReviewEntity;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {

    private Integer id;
    private Integer reportId;
    private int week;
    private MemberSimpleDto member;
    private ReviewStatus status;
    private String contents;

    public static ReviewResponseDto fromEntity(ReviewEntity entity) {
        MemberSimpleDto memberDto = new MemberSimpleDto(
                entity.getMember().getUuid(),
                entity.getMember().getName()
        );

        return ReviewResponseDto.builder()
                .id(entity.getId())
                .reportId(entity.getReportId())
                .week(entity.getWeek())
                .member(memberDto)
                .status(entity.getStatus())
                .contents(entity.getContents())
                .build();
    }
}