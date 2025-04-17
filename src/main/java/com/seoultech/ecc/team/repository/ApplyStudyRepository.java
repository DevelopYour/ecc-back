package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.ApplyStudyEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplyStudyRepository extends JpaRepository<ApplyStudyEntity, Long> {
    @Query("SELECT a.time FROM ApplyStudyEntity a")
    List<TimeEntity> findAllTimeEntities(); // 신청자가 존재하는 TimeEntity 리스트
}
