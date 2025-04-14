package com.seoultech.ecc.report.domain;

import lombok.Getter;

@Getter
public class Report {
    private final int week;
    private final String contents;
    private final int grade;

    public Report(int week, String contents, int grade) {
        this.week = week;
        this.contents = contents;
        this.grade = grade;
    }
}
