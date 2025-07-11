package com.seoultech.ecc.team.dto;

import com.seoultech.ecc.team.datamodel.ApplyRegularSubjectEntity;
import com.seoultech.ecc.team.datamodel.ApplyRegularTimeEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

public class ApplyStudyDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyRequest {
        @NotEmpty(message = "신청 과목 목록은 필수입니다.")
        private List<Integer> subjectIds;

        @NotEmpty(message = "신청 시간 목록은 필수입니다.")
        private List<Integer> timeIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplyResponse {
        private Integer memberUuid;
        private String memberName;
        private List<ApplySubjectDto> subjects;
        private List<ApplyTimeDto> times;

        @Getter
        @Builder
        public static class ApplySubjectDto {
            private Integer id;
            private Integer subjectId;
            private String subjectName;

            public static ApplySubjectDto fromEntity(ApplyRegularSubjectEntity entity) {
                return ApplySubjectDto.builder()
                        .id(entity.getId())
                        .subjectId(entity.getSubject().getId())
                        .subjectName(entity.getSubject().getName())
                        .build();
            }
        }

        @Getter
        @Builder
        public static class ApplyTimeDto {
            private Integer id;
            private Integer timeId;
            private TimeEntity.Day day;
            private Integer startTime;

            public static ApplyTimeDto fromEntity(ApplyRegularTimeEntity entity) {
                return ApplyTimeDto.builder()
                        .id(entity.getId())
                        .timeId(entity.getTime().getId())
                        .day(entity.getTime().getDay())
                        .startTime(entity.getTime().getStartTime())
                        .build();
            }
        }
    }
}