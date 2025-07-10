package com.seoultech.ecc.member.dto;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.team.datamodel.TeamMemberEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSimpleDto {
    private Integer id;
    private String studentId;
    private String name;

    public static MemberSimpleDto fromTeamMemberEntity(TeamMemberEntity entity) {
        return MemberSimpleDto.builder()
                .id(entity.getMember().getUuid())
                .studentId(entity.getMember().getStudentId())
                .name(entity.getMember().getName())
                .build();
    }

    public static MemberSimpleDto fromEntity(MemberEntity entity) {
        return MemberSimpleDto.builder()
                .id(entity.getUuid())
                .studentId(entity.getStudentId())
                .name(entity.getName())
                .build();
    }
}
