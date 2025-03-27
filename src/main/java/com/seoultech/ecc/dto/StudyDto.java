package com.seoultech.ecc.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyDto {
    private Long uuid;
    private Long teamId;
    private Long reportId;
    private Long subjectId;
    private String status;
}
