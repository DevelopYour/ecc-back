package com.seoultech.ecc.review.service;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.review.datamodel.ReviewDocument;
import com.seoultech.ecc.review.datamodel.ReviewTestDocument;
import com.seoultech.ecc.review.dto.QuestionDto;
import com.seoultech.ecc.review.dto.ReviewSummaryDto;
import com.seoultech.ecc.review.repository.ReviewRepository;
import com.seoultech.ecc.review.repository.ReviewTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
    // TODO: MongoDB 교체 후 수정
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
            ReviewDocument review = new ReviewDocument();
            review.setMember(member);
            review.setWeek(report.getWeek());
            review.setReportId(report.getId());
//            review.setContents(report.getContents()); // TODO: 추후 수정 필요
            reviewRepository.save(review);
        }
    }

    public ReviewTestDocument getReviewTest(Integer userId, String reviewId) {
        System.out.println("복습테스트 만들겡");
        // reviewId로 이미 진행 중인 reviewTest Redis 확인 후 있으면 반환
        ReviewTestDocument test = reviewTestRepository.findById(reviewId).orElse(null);
        if(test == null){
            System.out.println("null이라 복습테스트 만들겡");
            test = new ReviewTestDocument();
            test.setId(reviewId);
            test.setUserId(userId);
            test.setComplete(false);
            // TODO: ai
            List<QuestionDto> questions = new ArrayList<>();
            questions.add(new QuestionDto("질문1 예시 --", null, false));
            questions.add(new QuestionDto("질문2 예시 --", null, false));
            questions.add(new QuestionDto("질문3 예시 --", null, false));
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
