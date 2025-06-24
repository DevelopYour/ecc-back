package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.team.datamodel.ApplyRegularStudyEntity;
import com.seoultech.ecc.team.datamodel.SubjectEntity;
import com.seoultech.ecc.team.datamodel.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplyRegularStudyRepository extends JpaRepository<ApplyRegularStudyEntity, Integer> { // Long -> Integer 변경

    // 복잡한 쿼리는 @Query 유지
    @Query("SELECT a.time FROM ApplyRegularStudyEntity a")
    List<TimeEntity> findAllTimeEntities(); // 신청자가 존재하는 TimeEntity 리스트

    List<ApplyRegularStudyEntity> findByMember(MemberEntity member);

    @Modifying
    @Query("DELETE FROM ApplyRegularStudyEntity a WHERE a.member.uuid = :memberUuid")
    void deleteAllByMemberUuid(@Param("memberUuid") Integer memberUuid);

    // JPA 네이밍 컨벤션 적용
    List<ApplyRegularStudyEntity> findAllBySubjectSubjectId(Integer subjectId);
}