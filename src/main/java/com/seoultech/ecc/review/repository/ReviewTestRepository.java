package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewTestRepository extends JpaRepository<ReviewTestEntity, Integer> {

    // @Query 제거하고 JPA 네이밍 컨벤션 적용
    Optional<ReviewTestEntity> findByReviewId(Integer reviewId);

    List<ReviewTestEntity> findByReviewMemberUuid(Integer memberUuid);

    List<ReviewTestEntity> findByIsComplete(boolean isComplete);
}