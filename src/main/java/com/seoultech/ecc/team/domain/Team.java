package com.seoultech.ecc.team.domain;

import lombok.Getter;

@Getter
public class Team {
    private final String name;
    private final int score;
    private final int year;
    private final int semester;

    public Team(String name, int score, int year, int semester) {
        this.name = name;
        this.score = score;
        this.year = year;
        this.semester = semester;
    }

    public boolean isHonorTeam() {
        return score > 90;
    }
}