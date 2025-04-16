package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findAllByMember_Uuid(Integer memberId);

    List<ReviewEntity> findAllByReportIdAndMember_Uuid(String reportId, Integer memberId);

    List<ReviewEntity> findAllByReportId(String reportId);
}
