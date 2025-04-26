package com.seoultech.ecc.team.datamodel;

/**
 * 번개 스터디의 상태를 나타내는 열거형
 */
public enum OneTimeTeamStatus {
    RECRUITING,  // 모집 중 (신규 생성 시 기본 상태)
    UPCOMING,    // 모집 완료되었지만 아직 시작 시간이 되지 않은 상태
    IN_PROGRESS, // 진행 중 (스터디 시간에 진입)
    COMPLETED,   // 완료 (스터디 시간이 지남)
    CANCELED     // 취소됨 (개설자가 취소하거나 최소 인원 미달 등의 이유)
}