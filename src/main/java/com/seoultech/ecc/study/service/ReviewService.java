package com.seoultech.ecc.study.service;

import com.seoultech.ecc.member.dto.MemberSimpleDto;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.study.datamodel.ReviewDocument;
import com.seoultech.ecc.study.dto.ReviewSummaryDto;
import com.seoultech.ecc.study.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewDocument> findAllByMemberId(int memberId) {
        return reviewRepository.findAllByMemberId(memberId);
    }

    public ReviewDocument findAllByReportAndMember(String reportId, int memberId) {
        return reviewRepository.findByReportIdAndMemberId(reportId, memberId);
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
            review.setReportId(report.getId());
            review.setContents(report.getContents()); // TODO: 추후 수정 필요
            reviewRepository.save(review);
        }
    }
}
