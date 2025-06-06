package com.seoultech.ecc.team.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMatchResultDto {
    Long subjectId;
    List<TeamMatchDto> teamMatchDtoList;
    List<Long> failedMemberIdList;
}
