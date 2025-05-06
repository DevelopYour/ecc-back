package com.seoultech.ecc.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값 필드는 JSON 변환 시 제외
public class TeamDto {
    private Long teamId;
    private String name;
    private int score;
    private int year;
    private int semester;
    private Long subjectId;
    private String subjectName;
    private Integer timeId;
    private TimeEntity.Day day;
    private Integer startTime;
    private Integer studyCount;
    private Boolean isRegular;
    private List<MemberSimpleDto> members;
    private Boolean isCreator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    /**
     * 기본 정보만 포함한 DTO 생성 (목록 조회용)
     */
    public static TeamDto fromEntity(TeamEntity team) {
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .score(team.getScore())
                .year(team.getYear())
                .semester(team.getSemester())
                .subjectId(team.getSubject().getSubjectId())
                .build();
    }

    /**
     * 상세 정보를 포함한 DTO 생성 (상세 조회용)
     * uuid를 사용하도록 변경
     */
    public static TeamDto fromEntityWithDetails(TeamEntity team, Integer uuid) {
        // 팀 멤버 정보 변환
        List<MemberSimpleDto> members = team.getTeamMembers().stream()
                .map(tm -> new MemberSimpleDto(tm.getMember().getUuid(), tm.getMember().getName()))
                .collect(Collectors.toList());

        // 현재 사용자가 팀 생성자인지 확인
        // team.getCreatedBy()는 문자열(studentId)이므로
        // 팀 멤버에서 uuid로 멤버를 찾고 그 멤버의 studentId가 createdBy와 일치하는지 확인
        boolean isCreator = team.getTeamMembers().stream()
                .anyMatch(tm -> tm.getMember().getUuid().equals(uuid) &&
                        tm.getMember().getStudentId().equals(team.getCreatedBy()));

        return TeamDto.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .subjectId(team.getSubject().getSubjectId())
                .subjectName(team.getSubject().getName())
                .timeId(team.getTime().getTimeId())
                .day(team.getTime().getDay())
                .startTime(team.getTime().getStartTime())
                .year(team.getYear())
                .semester(team.getSemester())
                .score(team.getScore())
                .studyCount(team.getStudyCount())
                .isRegular(team.isRegular())
                .members(members)
                .isCreator(isCreator)
                .createdAt(team.getCreatedAt())
                .build();
    }
}