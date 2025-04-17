package com.seoultech.ecc.review.controller;

import com.seoultech.ecc.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
@Tag(name = "복습 API", description = "개인 복습 자료 관련 API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

//    @GetMapping("/me")
//    @Operation(summary = "복습자료 목록 조회", description = "로그인한 유저의 복습자료 목록을 조회합니다.")
//    public ResponseEntity<List<ReviewDto>> getMyReviews() {
//        return ResponseEntity.ok(reviewService.getMyReviewList());
//    }

//    @GetMapping("/me/{reviewId}")
//    @Operation(summary = "복습자료 조회", description = "특정 복습자료를 조회합니다.")
//    public ResponseEntity<ReviewDto> getMyReview(@PathVariable String reviewId) {
//        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
//    }
//
//    @PostMapping("/me/{reviewId}/test")
//    @Operation(summary = "복습 테스트 문제 요청", description = "특정 복습자료에 대한 테스트 문제를 요청합니다.")
//    public ResponseEntity<ReviewTestResponseDto> requestReviewTest(@PathVariable String reviewId) {
//        return ResponseEntity.ok(reviewService.generateReviewTest(reviewId));
//    }
//
//    @PatchMapping("/me/{reviewId}/test")
//    @Operation(summary = "복습 테스트 제출", description = "사용자의 테스트 답안을 제출합니다.")
//    public ResponseEntity<ReviewTestResponseDto> submitReviewTest(
//            @PathVariable String reviewId,
//            @RequestBody ReviewTestRequestDto requestDto) {
//        return ResponseEntity.ok(reviewService.submitReviewTest(reviewId, requestDto));
//    }
}
