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
    private Integer id;
    private String name;
    private int score;
    private int year;
    private int semester;
    private Integer subjectId;
    private String subjectName;
    private Integer timeId;
    private TimeEntity.Day day;
    private Integer startTime;
    private Integer studyCount;
    private Boolean regular;
    private List<MemberSimpleDto> members;
    private Boolean creator;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    /**
     * 기본 정보만 포함한 DTO 생성 (목록 조회용)
     */
    public static TeamDto fromEntity(TeamEntity team) {
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .score(team.getScore())
                .regular(team.isRegular())
                .year(team.getSemester().getYear())
                .semester(team.getSemester().getSemester())
                .day(team.getTime().getDay())
                .startTime(team.getTime().getStartTime())
                .subjectName(team.getSubject().getName())
                .subjectId(team.getSubject().getId())
                .regular(team.isRegular())
                .build();
    }

    /**
     * 상세 정보를 포함한 DTO 생성 (상세 조회용)
     * uuid를 사용하도록 변경
     */
    public static TeamDto fromEntityWithDetails(TeamEntity team, Integer uuid) {
        // 팀 멤버 정보 변환
        List<MemberSimpleDto> members = team.getTeamMembers().stream()
                .map(MemberSimpleDto::fromTeamMemberEntity)
                .collect(Collectors.toList());

        // 현재 사용자가 팀 생성자인지 확인
        boolean creator = uuid.equals(team.getCreatedBy());

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .subjectId(team.getSubject().getId())
                .subjectName(team.getSubject().getName())
                .timeId(team.getTime().getId())
                .day(team.getTime().getDay())
                .startTime(team.getTime().getStartTime())
                .year(team.getSemester().getYear())
                .semester(team.getSemester().getSemester())
                .score(team.getScore())
                .studyCount(team.getStudyCount())
                .regular(team.isRegular())
                .members(members)
                .creator(creator)
                .createdAt(team.getCreatedAt())
                .build();
    }
}