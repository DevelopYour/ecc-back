package com.seoultech.ecc.team.controller;

import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.team.dto.ApplyStudyDto;
import com.seoultech.ecc.team.service.ApplyStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams/regular/apply")
@RequiredArgsConstructor
@Tag(name = "정규 스터디 API", description = "정규 스터디 신청 관련 API")
@SecurityRequirement(name = "Bearer Authentication")
public class ApplyStudyController {

    private final ApplyStudyService regularStudyService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "정규 스터디 신청", description = "ACTIVE 상태의 회원이 정규 스터디를 신청합니다.")
    public ResponseEntity<ResponseDto<ApplyStudyDto.ApplyListResponse>> applyRegularStudy(
            @Valid @RequestBody ApplyStudyDto.ApplyRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        ApplyStudyDto.ApplyListResponse response = regularStudyService.applyRegularStudy(studentId, request);

        return ResponseEntity.ok(ResponseDto.success("정규 스터디 신청이 완료되었습니다.", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "정규 스터디 신청 내역 조회", description = "현재 로그인한 회원의 정규 스터디 신청 내역을 조회합니다.")
    public ResponseEntity<ResponseDto<ApplyStudyDto.ApplyListResponse>> getRegularStudyApplications() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        ApplyStudyDto.ApplyListResponse response = regularStudyService.getRegularStudyApplications(studentId);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "정규 스터디 신청 내역 수정", description = "현재 로그인한 회원의 정규 스터디 신청 내역을 수정합니다.")
    public ResponseEntity<ResponseDto<ApplyStudyDto.ApplyListResponse>> updateRegularStudyApplications(
            @Valid @RequestBody ApplyStudyDto.UpdateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        ApplyStudyDto.ApplyListResponse response = regularStudyService.updateRegularStudyApplications(studentId, request);

        return ResponseEntity.ok(ResponseDto.success("정규 스터디 신청 내역이 수정되었습니다.", response));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "정규 스터디 신청 취소", description = "현재 로그인한 회원의 정규 스터디 신청을 모두 취소합니다.")
    public ResponseEntity<ResponseDto<Void>> cancelRegularStudyApplications() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();

        regularStudyService.cancelRegularStudyApplications(studentId);

        return ResponseEntity.ok(ResponseDto.success("정규 스터디 신청이 취소되었습니다.", null));
    }
}