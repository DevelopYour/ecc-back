package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewTestRepository extends JpaRepository<ReviewTestEntity, Integer> {

    @Query("SELECT rt FROM ReviewTestEntity rt WHERE rt.review.id = :reviewId")
    Optional<ReviewTestEntity> findByReviewId(@Param("reviewId") Integer reviewId);

    @Query("SELECT rt FROM ReviewTestEntity rt WHERE rt.review.member.uuid = :userId")
    List<ReviewTestEntity> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT rt FROM ReviewTestEntity rt WHERE rt.isComplete = :isComplete")
    List<ReviewTestEntity> findByIsComplete(@Param("isComplete") boolean isComplete);
}