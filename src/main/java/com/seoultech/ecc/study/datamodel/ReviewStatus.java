package com.seoultech.ecc.study.datamodel;

public enum ReviewStatus {
    NOT_READY,     // 복습 불가 (스터디 미완료)
    NOT_CREATED,   // 복습자료 아직 생성되지 않음
    INCOMPLETE,    // 복습자료 있음, 테스트 미완료
    COMPLETED      // 복습 완료
}

