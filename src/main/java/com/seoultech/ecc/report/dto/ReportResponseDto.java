package com.seoultech.ecc.report.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportEntity;
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
public class ReportResponseDto {

    private String id; // 기존 MongoDB의 String id 호환성 유지
    private Integer teamId;
    private Integer subjectId;
    private List<MemberSimpleDto> members;
    private int week;
    private String contents;
    private int grade;
    private boolean isSubmitted;

    public static ReportResponseDto fromEntity(ReportEntity entity) {
        List<MemberSimpleDto> members = entity.getReportMembers().stream()
                .map(rm -> new MemberSimpleDto(rm.getMember().getUuid(), rm.getMember().getName()))
                .collect(Collectors.toList());

        return ReportResponseDto.builder()
                .id(entity.getId().toString()) // Integer를 String으로 변환하여 호환성 유지
                .teamId(entity.getTeamId())
                .subjectId(entity.getSubjectId())
                .members(members)
                .week(entity.getWeek())
                .contents(entity.getContents())
                .grade(entity.getGrade())
                .isSubmitted(entity.isSubmitted())
                .build();
    }
}