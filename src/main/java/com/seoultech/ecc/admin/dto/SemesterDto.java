package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.admin.datamodel.SemesterEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDto {
    private Integer id;
    private Integer year;
    private Integer semester;

    public static SemesterDto fromEntity(SemesterEntity entity) {
        return SemesterDto.builder()
                .id(entity.getId())
                .year(entity.getYear())
                .semester(entity.getSemester())
                .build();
    }
}
