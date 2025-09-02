package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.dto.AdminSummaryDto;
import com.seoultech.ecc.admin.dto.SemesterDto;
import com.seoultech.ecc.admin.service.AdminSettingsService;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.team.service.ApplyStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/setting/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 설정 관련 API", description = "관리자 설정 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminSettingController {

    private final AdminSettingsService settingService;
    private final ApplyStudyService applyStudyService;

    @GetMapping("semester")
    @Operation(summary = "현재 학기 조회", description = "현재 설정된 연도 및 학기 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<SemesterDto>> getCurrentSemester() {
        SemesterDto semester = settingService.getCurrentSemester();
        return ResponseEntity.ok(ResponseDto.success(semester));
    }

    @PostMapping("semester")
    @Operation(summary = "현재 학기 갱신", description = "새로운 학기 데이터로 갱신합니다.")
    public ResponseEntity<ResponseDto<Boolean>> updateCurrentSemester(SemesterDto dto) {
        settingService.updateCurrentSemester(dto);
        return ResponseEntity.ok(ResponseDto.success(true));
    }

    // applystudyservice로 옮겨
    @GetMapping("study-recruitment")
    @Operation(summary = "정규스터디 모집 여부 조회", description = "현재 학기의 정규스터디 모집 여부 상태를 조회합니다.")
    public ResponseEntity<ResponseDto<Boolean>> getRecruitmentStatus() {
        return ResponseEntity.ok(ResponseDto.success(applyStudyService.getRecruitmentStatus()));
    }

    @PutMapping("study-recruitmet")
    @Operation(summary = "정규스터디 모집 여부 수정", description = "현재 학기의 정규스터디 모집 여부를 수정합니다.")
    public ResponseEntity<ResponseDto<Boolean>> getCurrentSemester(Boolean status) {
        settingService.setRecruitmentStatus(status);
        return ResponseEntity.ok(ResponseDto.success(true));
    }
}
