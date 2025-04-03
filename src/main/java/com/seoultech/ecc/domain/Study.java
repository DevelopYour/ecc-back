package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class Study {
    private final String status;

    public Study(String status) {
        this.status = status;
    }

}
