package com.seoultech.ecc.study.domain;

import lombok.Getter;

@Getter
public class Study {
    private final String status;

    public Study(String status) {
        this.status = status;
    }

}
