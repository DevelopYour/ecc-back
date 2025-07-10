package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTeamDto {
    private List<MemberSimpleDto> members;
    private TimeEntity.Day day;
    private Integer subjectId;
    private String subjectName;
    private Integer timeId;
    private Integer startTime;
}
