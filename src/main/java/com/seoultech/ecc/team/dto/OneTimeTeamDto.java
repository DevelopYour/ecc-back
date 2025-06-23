package com.seoultech.ecc.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.team.datamodel.OneTimeTeamStatus;
import com.seoultech.ecc.team.datamodel.TeamEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OneTimeTeamDto {

    /**
     * 번개 스터디 생성 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        @NotBlank(message = "번개 스터디 이름은 필수입니다.")
        private String name;

        @NotNull(message = "과목 ID는 필수입니다.")
        private Integer subjectId; // Long → Integer 변경

        @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다.")
        @Max(value = 5, message = "최대 인원은 5명 이하여야 합니다.")
        private int maxMembers = 5;

        @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다.")
        private int minMembers = 2;

        @NotNull(message = "시작 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime startTime;

        @NotNull(message = "종료 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime endTime;

        private String description;

        private String location;
    }

    /**
     * 번개 스터디 수정 요청 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @Size(min = 2, max = 50, message = "스터디 이름은 2~50자 사이여야 합니다.")
        private String name;

        private Integer subjectId; // Long → Integer 변경

        @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다.")
        @Max(value = 5, message = "최대 인원은 5명 이하여야 합니다.")
        private Integer maxMembers;

        @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다.")
        private Integer minMembers;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime startTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime endTime;

        @Size(max = 500, message = "설명은 최대 500자까지 입력 가능합니다.")
        private String description;

        @Size(max = 100, message = "위치 정보는 최대 100자까지 입력 가능합니다.")
        private String location;
    }

    /**
     * 번개 스터디 응답 DTO (목록 조회용)
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer teamId; // Long → Integer 변경
        private String name;
        private Integer subjectId; // Long → Integer 변경
        private String subjectName;
        private List<MemberSimpleDto> members;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime startTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime endTime;

        private int maxMembers;
        private int currentMembers;
        private int minMembers;
        private OneTimeTeamStatus status;
        private String description;
        private String location;
        private Integer createdBy;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime createdAt;

        public static Response fromEntity(TeamEntity entity) {
            if (entity == null || entity.getOneTimeInfo() == null) {
                throw new IllegalArgumentException("유효하지 않은 번개 스터디 엔티티입니다.");
            }

            List<MemberSimpleDto> memberDtos = entity.getTeamMembers().stream()
                    .map(tm -> new MemberSimpleDto(tm.getMember().getUuid(), tm.getMember().getName()))
                    .collect(Collectors.toList());

            return Response.builder()
                    .teamId(entity.getTeamId())
                    .name(entity.getName())
                    .subjectId(entity.getSubject().getSubjectId())
                    .subjectName(entity.getSubject().getName())
                    .startTime(entity.getOneTimeInfo().getStartTime())
                    .endTime(entity.getOneTimeInfo().getEndTime())
                    .maxMembers(entity.getOneTimeInfo().getMaxMembers())
                    .currentMembers(entity.getTeamMembers().size())
                    .minMembers(entity.getOneTimeInfo().getMinMembers())
                    .status(entity.getOneTimeInfo().getStatus())
                    .description(entity.getOneTimeInfo().getDescription())
                    .location(entity.getOneTimeInfo().getLocation())
                    .createdBy(entity.getCreatedBy())
                    .createdAt(entity.getCreatedAt())
                    .members(memberDtos)
                    .build();
        }
    }

    /**
     * 번개 스터디 상세 정보 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResponse {
        private Integer teamId; // Long → Integer 변경
        private String name;
        private Integer subjectId; // Long → Integer 변경
        private String subjectName;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime startTime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime endTime;

        private int maxMembers;
        private int currentMembers;
        private int minMembers;
        private OneTimeTeamStatus status;
        private String description;
        private String location;
        private Integer createdBy;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        private LocalDateTime createdAt;

        private List<MemberSimpleDto> members;
        private boolean canJoin;
        private boolean canCancel;
        private boolean isCreator;

        public static DetailResponse fromEntity(TeamEntity entity, Integer uuid) {
            if (entity == null || entity.getOneTimeInfo() == null) {
                throw new IllegalArgumentException("유효하지 않은 번개 스터디 엔티티입니다.");
            }

            // 로그인한 회원(uuid)이 팀의 생성자인지 확인
            boolean isCreator = uuid.equals(entity.getCreatedBy());

            List<MemberSimpleDto> memberDtos = entity.getTeamMembers().stream()
                    .map(tm -> new MemberSimpleDto(tm.getMember().getUuid(), tm.getMember().getName()))
                    .collect(Collectors.toList());

            return DetailResponse.builder()
                    .teamId(entity.getTeamId())
                    .name(entity.getName())
                    .subjectId(entity.getSubject().getSubjectId())
                    .subjectName(entity.getSubject().getName())
                    .startTime(entity.getOneTimeInfo().getStartTime())
                    .endTime(entity.getOneTimeInfo().getEndTime())
                    .maxMembers(entity.getOneTimeInfo().getMaxMembers())
                    .currentMembers(entity.getTeamMembers().size())
                    .minMembers(entity.getOneTimeInfo().getMinMembers())
                    .status(entity.getOneTimeInfo().getStatus())
                    .description(entity.getOneTimeInfo().getDescription())
                    .location(entity.getOneTimeInfo().getLocation())
                    .createdBy(entity.getCreatedBy())
                    .createdAt(entity.getCreatedAt())
                    .members(memberDtos)
                    .canJoin(entity.getOneTimeInfo().isApplicable())
                    .canCancel(entity.getOneTimeInfo().isCancelable())
                    .isCreator(isCreator)
                    .build();
        }
    }

    /**
     * 번개 스터디 목록 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private List<Response> teams;

        public static ListResponse fromEntities(List<TeamEntity> entities) {
            List<Response> responses = entities.stream()
                    .filter(entity -> entity.getOneTimeInfo() != null)
                    .map(Response::fromEntity)
                    .collect(Collectors.toList());

            return ListResponse.builder()
                    .teams(responses)
                    .build();
        }
    }
}