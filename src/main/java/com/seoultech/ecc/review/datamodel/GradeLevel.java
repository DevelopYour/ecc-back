package com.seoultech.ecc.review.datamodel;

public enum GradeLevel {
    CORRECT,    // 완전 정답
    PARTIAL,    // 부분 점수
    INCORRECT;   // 오답

    public static GradeLevel fromIntGrade(Integer grade) {
        return switch (grade) {
            case 2 -> GradeLevel.CORRECT;
            case 1 -> GradeLevel.PARTIAL;
            case 0 -> GradeLevel.INCORRECT;
            default -> GradeLevel.INCORRECT;
        };
    }
}
