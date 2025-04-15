package com.seoultech.ecc.study.datamodel;

public enum StudyStatus {
    ONGOING, // 진행 중 (REDIS HASH 존재)
    DRAFTING, // 보고서 작성 중
    COMPLETE
}
