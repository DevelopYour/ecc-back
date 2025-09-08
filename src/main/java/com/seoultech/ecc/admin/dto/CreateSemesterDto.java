package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.admin.datamodel.SemesterEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSemesterDto {
    private Integer year;
    private Integer semester;
}
