package com.seoultech.ecc.admin.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingDto {
    private SemesterDto currentSemester; // 현재 학기
    private Boolean isRecruiting; // 현재 학기 스터디 모집 여부
    private List<SemesterDto> semesters; // 등록된 전체 학기
}
