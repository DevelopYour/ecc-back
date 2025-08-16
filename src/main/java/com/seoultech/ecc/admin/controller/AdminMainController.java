package com.seoultech.ecc.admin.controller;

import com.seoultech.ecc.admin.dto.AdminSummaryDto;
import com.seoultech.ecc.admin.service.AdminMainService;
import com.seoultech.ecc.member.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/main/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 대시보드 관련 API", description = "관리자 대시보드 API")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminMainController {

    private final AdminMainService adminService;

    @GetMapping("summary")
    @Operation(summary = "전체 주제 카테고리 조회", description = "주제 카테고리 전체 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<AdminSummaryDto>> getAllCategories() {
        AdminSummaryDto summary = adminService.getSummary();
        return ResponseEntity.ok(ResponseDto.success(summary));
    }

}
