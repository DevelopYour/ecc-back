package com.seoultech.ecc.team.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectDto {
    private Long subjectId;
    private String name;
    private String template;
    private String prompt;
}
