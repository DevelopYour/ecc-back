package com.seoultech.ecc.domain;

import lombok.Getter;

@Getter
public class MemberDomain {
    private final String userId;
    private final String name;
    private final int level;

    public MemberDomain(String userId, String name, int level) {
        this.userId = userId;
        this.name = name;
        this.level = level;
    }

    public boolean isEligibleForUpgrade() {
        return level >= 10;
    }
}
