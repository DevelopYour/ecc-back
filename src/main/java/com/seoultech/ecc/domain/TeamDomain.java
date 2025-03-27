package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class TeamDomain {
    private final String name;
    private final int score;
    private final int year;
    private final int semester;

    public TeamDomain(String name, int score, int year, int semester) {
        this.name = name;
        this.score = score;
        this.year = year;
        this.semester = semester;
    }

    public boolean isHonorTeam() {
        return score > 90;
    }
}