package com.seoultech.ecc.review.repository;

import com.seoultech.ecc.review.datamodel.ReviewEntity;
import com.seoultech.ecc.review.datamodel.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    @Query("SELECT r FROM ReviewEntity r WHERE r.member.uuid = :memberId ORDER BY r.createdAt DESC")
    List<ReviewEntity> findAllByMemberId(@Param("memberId") Integer memberId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.report.id = :reportId")
    List<ReviewEntity> findAllByReportId(@Param("reportId") Integer reportId);

    @Query("SELECT r FROM ReviewEntity r WHERE r.status = :status")
    List<ReviewEntity> findByStatus(@Param("status") ReviewStatus status);

    @Query("SELECT r FROM ReviewEntity r WHERE r.member.uuid = :memberId AND r.status = :status")
    List<ReviewEntity> findByMemberIdAndStatus(@Param("memberId") Integer memberId, @Param("status") ReviewStatus status);
}