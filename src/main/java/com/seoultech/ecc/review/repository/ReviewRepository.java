package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewEntity;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    // @Query 제거하고 JPA 네이밍 컨벤션 적용
    List<ReviewEntity> findAllByMemberUuidOrderByCreatedAtDesc(Integer memberUuid);

    List<ReviewEntity> findAllByReport_Id(Integer reportId);

    List<ReviewEntity> findByStatus(ReviewStatus status);

    List<ReviewEntity> findByMemberUuidAndStatus(Integer memberUuid, ReviewStatus status);
}