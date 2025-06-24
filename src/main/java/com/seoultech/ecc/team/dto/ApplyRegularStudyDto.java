package com.seoultech.ecc.team.dto;

import com.seoultech.ecc.team.datamodel.ApplyRegularStudyEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyRegularStudyDto {

    private Integer id;
    private Integer memberId;
    private Integer subjectId;
    private Integer timeId;

    public static ApplyRegularStudyDto fromEntity(ApplyRegularStudyEntity entity) {
        return ApplyRegularStudyDto.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getUuid())
                .subjectId(entity.getSubject().getSubjectId())
                .timeId(entity.getTime().getTimeId())
                .build();
    }
}
