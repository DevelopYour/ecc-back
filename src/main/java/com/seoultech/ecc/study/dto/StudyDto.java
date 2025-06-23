package com.seoultech.ecc.study.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyDto {
    private String uuid;
    private Integer teamId;
    private Integer reportId;
    private Integer subjectId;
    private String status;
}
