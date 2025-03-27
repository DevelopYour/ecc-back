package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class StudyDomain {
    private final String status;

    public StudyDomain(String status) {
        this.status = status;
    }

    public boolean isOngoing() {
        return "ONGOING".equalsIgnoreCase(status);
    }
}
