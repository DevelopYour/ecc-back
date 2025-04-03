package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class Major {
    private final String name;
    private final String college;

    public Major(String name, String college) {
        this.name = name;
        this.college = college;
    }
}
