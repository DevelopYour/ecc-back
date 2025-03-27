package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class ReportDomain {
    private final int week;
    private final String contents;
    private final int grade;

    public ReportDomain(int week, String contents, int grade) {
        this.week = week;
        this.contents = contents;
        this.grade = grade;
    }

    public boolean isExcellentReport() {
        return grade == 3;
    }
}
