package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyEnterDto {
    private String studyId;
    private Boolean isGeneral;
}
