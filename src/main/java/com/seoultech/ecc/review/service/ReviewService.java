package com.seoultech.ecc.review.service;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.dto.ReportFeedbackDto;
import com.seoultech.ecc.review.datamodel.ReviewDocument;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import com.seoultech.ecc.review.datamodel.ReviewTestDocument;
import com.seoultech.ecc.review.dto.ReviewQuestionDto;
import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import com.seoultech.ecc.review.repository.ReviewRepository;
import com.seoultech.ecc.review.repository.ReviewTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewTestRepository reviewTestRepository;

    public List<ReviewDocument> findAllByMemberId(int memberId) {
        return reviewRepository.findAllByMemberId(memberId);
    }

    public ReviewDocument findByReviewId(String reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    // reportId로 팀원별 복습 현황 상태 확인
    public List<ReviewSummaryDto> getReviewStatusInfos(String reportId) {
        List<ReviewDocument> reviews = reviewRepository.findAllByReportId(reportId);
        List<ReviewSummaryDto> dtos = new ArrayList<>();
        for (ReviewDocument review : reviews) {
            ReviewSummaryDto dto = new ReviewSummaryDto();
            dto.setReviewId(review.getId());
            dto.setMemberId(review.getMember().getId());
            dto.setMemberName(review.getMember().getName());
            dto.setReviewStatus(review.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }

    public void createReviews(ReportDocument report){
        for(MemberSimpleDto member: report.getMembers()){
            ReviewDocument review = ReviewDocument.fromReport(report);
            review.setMember(member);
            review.setStatus(ReviewStatus.INCOMPLETE);
            reviewRepository.save(review);
        }
    }

    // TODO: 개인 맞춤형 문항 추가 필요
    // 현재) ReportDocument에서 번역-예문, 피드백-교정문 영작 문제로 출제 (추후 AI 활용 고민)
    public ReviewTestDocument getReviewTest(Integer userId, String reviewId) {
        // reviewId로 이미 생성된 reviewTest 확인 후 있으면 반환
        ReviewTestDocument test = reviewTestRepository.findById(reviewId).orElse(null);

        if(test == null){
            test = new ReviewTestDocument();
            test.setId(reviewId);
            test.setUserId(userId);
            test.setComplete(false);

            ReviewDocument review = reviewRepository.findById(reviewId).orElse(null);
            List<ReviewQuestionDto> questions = new ArrayList<>();
            if(review.getTopics() != null) {
                // feedback 리스트로 문제 생성
                review.getTopics().stream().filter(topic -> topic != null && topic.getFeedbacks() != null).forEach(topic ->
                        topic.getFeedbacks().stream().filter(Objects::nonNull).forEach(feedback ->
                                questions.add(ReviewQuestionDto.fromFeedback(feedback)))
                );
                // translation 리스트로 문제 생성
                review.getTopics().stream().filter(topic -> topic != null && topic.getTranslations() != null).forEach(topic ->
                        topic.getTranslations().stream().filter(Objects::nonNull).forEach(translation ->
                                questions.add(ReviewQuestionDto.fromTranslation(translation)))
                );
            }
            test.setQuestions(questions);
            test = reviewTestRepository.save(test);
        }
        return test;
    }

    public ReviewTestDocument submitReviewTest(ReviewTestDocument test) {
        // TODO: ai
//        ReviewDocument test = aiService.gradeTest(test);
        test.setComplete(true);
        return test;
    }

}
