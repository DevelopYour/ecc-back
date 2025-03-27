package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class MajorDomain {
    private final String name;
    private final String college;

    public MajorDomain(String name, String college) {
        this.name = name;
        this.college = college;
    }
}
