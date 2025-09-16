package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewDocument, String> {
    List<ReviewDocument> findAllByMemberId(Integer memberId);

    List<ReviewDocument> findAllByReportId(String reportId);

    ReviewDocument findByReportIdAndMemberId(String reportId, Integer memberId);
}
