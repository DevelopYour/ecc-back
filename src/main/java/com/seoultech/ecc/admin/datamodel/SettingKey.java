package com.seoultech.ecc.admin.datamodel;

import lombok.Getter;

@Getter
public enum SettingKey {
    CURRENT_SEMESTER_ID("current_semester_id", "현재 학기 ID"),
    RECRUITMENT_STATUS("recruitment_status", "정규 스터디 모집 상태"),
    AUTO_TEAM_MATCHING_ENABLED("auto_team_matching_enabled", "자동팀매칭 기능 사용 가능 여부");

    private final String key;
    private final String description;

    SettingKey(String key, String description) {
        this.key = key;
        this.description = description;
    }

}
