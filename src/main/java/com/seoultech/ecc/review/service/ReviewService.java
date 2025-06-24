package com.seoultech.ecc.review.service;

import com.seoultech.ecc.member.repository.MemberRepository;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.datamodel.ReportMemberEntity;
import com.seoultech.ecc.review.datamodel.ReviewEntity;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import com.seoultech.ecc.review.datamodel.ReviewTestEntity;
import com.seoultech.ecc.review.datamodel.ReviewTestQuestionEntity;
import com.seoultech.ecc.review.dto.QuestionDto;
import com.seoultech.ecc.review.dto.ReviewResponseDto;
import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import com.seoultech.ecc.review.dto.ReviewTestResponseDto;
import com.seoultech.ecc.review.repository.ReviewRepository;
import com.seoultech.ecc.review.repository.ReviewTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewTestRepository reviewTestRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Repository 메서드명 변경에 따른 수정
    public List<ReviewResponseDto> findAllByMemberId(Integer memberId) {
        List<ReviewEntity> entities = reviewRepository.findAllByMemberUuidOrderByCreatedAtDesc(memberId);
        return entities.stream()
                .map(ReviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // String → Integer 변경, parseInt 제거
    public ReviewResponseDto findByReviewId(Integer reviewId) {
        ReviewEntity entity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        return ReviewResponseDto.fromEntity(entity);
    }

    // String → Integer 변경, parseInt 제거
    public ReviewEntity findEntityByReviewId(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
    }

    // reportId를 String → Integer 변경, parseInt 제거
    public List<ReviewSummaryDto> getReviewStatusInfos(Integer reportId) {
        List<ReviewEntity> reviews = reviewRepository.findAllByReport_Id(reportId);

        return reviews.stream()
                .map(review -> ReviewSummaryDto.builder()
                        .reviewId(review.getId())
                        .memberId(review.getMember().getUuid())
                        .memberName(review.getMember().getName())
                        .reviewStatus(review.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createReviews(ReportEntity report) {
        for (ReportMemberEntity reportMember : report.getReportMembers()) {
            ReviewEntity review = new ReviewEntity();
            review.setMember(reportMember.getMember());
            review.setWeek(report.getWeek());
            review.setReport(report);
            review.setContents(report.getContents());
            review.setStatus(ReviewStatus.INCOMPLETE); // 보고서가 제출되면 복습 가능 상태

            reviewRepository.save(review);
        }
    }

    @Transactional
    public ReviewTestResponseDto getReviewTest(Integer userId, Integer reviewId) { // String → Integer 변경
        // 기존 테스트가 있는지 확인
        ReviewTestEntity existingTest = reviewTestRepository.findByReviewId(reviewId).orElse(null);

        if (existingTest != null) {
            return ReviewTestResponseDto.fromEntity(existingTest);
        }

        // 새로운 테스트 생성
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        ReviewTestEntity test = new ReviewTestEntity();
        test.setReview(review);
        test.setComplete(false);

        // TODO: AI로 질문 생성
        List<ReviewTestQuestionEntity> questions = new ArrayList<>();
        String[] sampleQuestions = {
                "질문1 예시 --",
                "질문2 예시 --",
                "질문3 예시 --"
        };

        for (int i = 0; i < sampleQuestions.length; i++) {
            ReviewTestQuestionEntity question = new ReviewTestQuestionEntity();
            question.setReviewTest(test);
            question.setQuestion(sampleQuestions[i]);
            question.setQuestionOrder(i + 1);
            question.setCorrect(false);
            questions.add(question);
        }

        test.setQuestions(questions);
        ReviewTestEntity saved = reviewTestRepository.save(test);

        return ReviewTestResponseDto.fromEntity(saved);
    }

    @Transactional
    public ReviewTestResponseDto submitReviewTest(ReviewTestResponseDto testDto) {
        // String id를 Integer로 변환
        Integer id = Integer.valueOf(testDto.getId());
        ReviewTestEntity test = reviewTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review test not found with id: " + testDto.getId()));

        // 답안 업데이트
        List<QuestionDto> submittedQuestions = testDto.getQuestions();
        List<ReviewTestQuestionEntity> existingQuestions = test.getQuestions();

        for (int i = 0; i < Math.min(submittedQuestions.size(), existingQuestions.size()); i++) {
            ReviewTestQuestionEntity existing = existingQuestions.get(i);
            QuestionDto submitted = submittedQuestions.get(i);

            existing.setAnswer(submitted.getAnswer());
            existing.setCorrect(submitted.isCorrect());
        }

        // TODO: AI로 채점
        test.setComplete(true);

        // 복습 완료 상태로 변경
        ReviewEntity review = test.getReview();
        review.setStatus(ReviewStatus.COMPLETED);
        reviewRepository.save(review);

        ReviewTestEntity saved = reviewTestRepository.save(test);
        return ReviewTestResponseDto.fromEntity(saved);
    }

    // 호환성을 위한 추가 메서드들
    public ReviewTestResponseDto submitReviewTestByEntity(ReviewTestEntity testEntity) {
        return ReviewTestResponseDto.fromEntity(testEntity);
    }
}