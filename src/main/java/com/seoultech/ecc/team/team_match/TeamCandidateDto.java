package com.seoultech.ecc.team.team_match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamCandidateDto {
    private int timeId;
    private List<Integer> memberIds;

    public String toString(){
        return "시간 " + timeId + " 팀원 " + memberIds;
    }
}
