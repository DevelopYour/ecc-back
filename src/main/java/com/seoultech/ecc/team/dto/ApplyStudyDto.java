package com.seoultech.ecc.team.dto;

import com.seoultech.ecc.team.datamodel.ApplyRegularStudyEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class ApplyStudyDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyRequest {
        @NotEmpty(message = "신청 과목 목록은 필수입니다.")
        private List<Integer> subjectIds; // Long → Integer 변경

        @NotEmpty(message = "신청 시간 목록은 필수입니다.")
        private List<Integer> timeIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        @NotEmpty(message = "신청 과목 목록은 필수입니다.")
        private List<Integer> subjectIds; // Long → Integer 변경

        @NotEmpty(message = "신청 시간 목록은 필수입니다.")
        private List<Integer> timeIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyResponse {
        private Integer id; // Long → Integer 변경
        private Integer memberUuid;
        private String memberName;
        private Integer subjectId; // Long → Integer 변경
        private String subjectName;
        private Integer timeId;
        private TimeEntity.Day day;
        private int startTime;

        public static ApplyResponse fromEntity(ApplyRegularStudyEntity entity) {
            return ApplyResponse.builder()
                    .id(entity.getId())
                    .memberUuid(entity.getMember().getUuid())
                    .memberName(entity.getMember().getName())
                    .subjectId(entity.getSubject().getSubjectId())
                    .subjectName(entity.getSubject().getName())
                    .timeId(entity.getTime().getTimeId())
                    .day(entity.getTime().getDay())
                    .startTime(entity.getTime().getStartTime())
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyListResponse {
        private List<ApplyResponse> applications;

        public static ApplyListResponse fromEntityList(List<ApplyRegularStudyEntity> entities) {
            List<ApplyResponse> responses = entities.stream()
                    .map(ApplyResponse::fromEntity)
                    .collect(Collectors.toList());

            return ApplyListResponse.builder()
                    .applications(responses)
                    .build();
        }
    }
}