package com.seoultech.ecc.team.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {
    private Long teamId;
    private String name;
    private int score;
    private int year;
    private int semester;
    private Long subjectId;
}
