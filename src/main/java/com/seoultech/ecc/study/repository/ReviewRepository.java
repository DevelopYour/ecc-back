package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.ReviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewDocument, String> {
    List<ReviewDocument> findAllByMemberId(Integer memberId);

    ReviewDocument findByReportIdAndMemberId(String reportId, Integer memberId);

    List<ReviewDocument> findAllByReportId(String reportId);
}
