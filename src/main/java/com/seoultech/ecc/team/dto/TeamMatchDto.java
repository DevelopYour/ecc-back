package com.seoultech.ecc.team.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMatchDto {
    private Integer timeId;
    private List<Long> memberIds; // 3~5명의 유저 아이디
}
