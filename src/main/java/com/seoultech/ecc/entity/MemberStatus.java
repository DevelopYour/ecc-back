package com.seoultech.ecc.entity;

public enum MemberStatus {
    ACTIVE,       // 정상 활동 중 (서비스 이용 가능)
    PENDING,      // 가입 승인 대기 (운영진 승인 전)
    SUSPENDED,    // 일시 정지 (강제 탈퇴 심사 중: 규칙 위반 등으로 임시 제한 조치)
    BANNED,       // 강제 탈퇴 (규칙 위반 등으로 탈퇴 처리됨)
    WITHDRAWN,    // 자발적 탈퇴 (회원 요청으로 탈퇴)
    DORMANT,      // 휴면 계정 (재등록 미완료)
    DORMANT_REQUESTED // 휴면 해제 대기중 (운영진 승인 전)
}
