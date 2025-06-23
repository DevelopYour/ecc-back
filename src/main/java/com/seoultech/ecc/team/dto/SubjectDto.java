package com.seoultech.ecc.team.dto;

import com.seoultech.ecc.team.datamodel.SubjectEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDto {
    private Integer subjectId; // Long → Integer 변경
    private String name;

    public static SubjectDto fromEntity(SubjectEntity subject) {
        return SubjectDto.builder()
                .subjectId(subject.getSubjectId())
                .name(subject.getName())
                .build();
    }
}