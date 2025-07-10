package com.seoultech.ecc.admin.dto;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedTeamDto {
    private List<MemberSimpleDto> members;
    private Integer subjectId;
    private Integer timeId;
}
