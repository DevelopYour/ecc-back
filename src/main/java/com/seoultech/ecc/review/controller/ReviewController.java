package com.seoultech.ecc.review.controller;

import com.seoultech.ecc.member.dto.CustomUserDetails;
import com.seoultech.ecc.member.dto.ResponseDto;
import com.seoultech.ecc.review.datamodel.ReviewDocument;
import com.seoultech.ecc.review.datamodel.ReviewTestDocument;
import com.seoultech.ecc.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Tag(name = "복습 API", description = "개인 복습 자료 관련 API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/me")
    @Operation(summary = "복습자료 목록 조회", description = "로그인한 유저의 복습자료 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<List<ReviewDocument>>> getMyReviews(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(reviewService.findAllByMemberId(userDetails.getId())));
    }

    @GetMapping("/me/{reviewId}")
    @Operation(summary = "복습자료 조회", description = "특정 복습자료를 조회합니다.")
    public ResponseEntity<ResponseDto<ReviewDocument>> getMyReview(@PathVariable String reviewId) {
        // TODO: 권한 확인
        return ResponseEntity.ok(ResponseDto.success(reviewService.findByReviewId(reviewId)));
    }

    @PostMapping("/me/{reviewId}/test")
    @Operation(summary = "복습 테스트 문제 요청", description = "특정 복습자료에 대한 테스트 문제를 요청합니다.")
    public ResponseEntity<ResponseDto<ReviewTestDocument>> requestReviewTest(@PathVariable String reviewId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ResponseDto.success(reviewService.getReviewTest(userDetails.getId(), reviewId)));
    }

    @PatchMapping("/me/{reviewId}/test")
    @Operation(summary = "복습 테스트 제출", description = "사용자의 테스트 답안을 제출합니다.")
    public ResponseEntity<ResponseDto<ReviewTestDocument>> submitReviewTest(@PathVariable String reviewId,
                                                                            @RequestBody ReviewTestDocument test) {
        return ResponseEntity.ok(ResponseDto.success(reviewService.submitReviewTest(test)));
    }
}
