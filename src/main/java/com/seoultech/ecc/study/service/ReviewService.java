package com.seoultech.ecc.study.service;

import com.seoultech.ecc.study.datamodel.ReviewEntity;
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

    public List<ReviewEntity> findAllByMemberId(Long memberId) {
        return reviewRepository.findAllByMember_Uuid(memberId);
    }

    public List<ReviewEntity> findAllByReportAndMember(Long reportId, Long memberId) {
        return reviewRepository.findAllByReport_ReportIdAndMember_Uuid(reportId, memberId);
    }

    // reportId로 팀원별 복습 현황 상태 확인
    public List<ReviewSummaryDto> getReviewStatusInfos(Long reportId) {
        List<ReviewEntity> reviews = reviewRepository.findAllByReport_ReportId(reportId);
        List<ReviewSummaryDto> dtos = new ArrayList<>();
        for (ReviewEntity review : reviews) {
            ReviewSummaryDto dto = new ReviewSummaryDto();
            dto.setReviewId(review.getReviewId());
            dto.setMemberId(review.getMember().getStudentId());
            dto.setMemberName(review.getMember().getName());
            dto.setReviewStatus(review.getStatus());
            dtos.add(dto);
        }
        return dtos;
    }
}
