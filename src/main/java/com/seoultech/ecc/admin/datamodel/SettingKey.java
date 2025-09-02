package com.seoultech.ecc.admin.datamodel;

public enum SettingKey {
    CURRENT_SEMESTER_ID("current_semester_id", "현재 학기 ID"),
    RECRUITMENT_STATUS("recruitment_status", "정규 스터디 모집 상태");

    private final String key;
    private final String description;

    SettingKey(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() { return key; }
    public String getDescription() { return description; }
}
