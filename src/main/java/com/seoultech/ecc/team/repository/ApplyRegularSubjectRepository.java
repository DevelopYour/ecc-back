package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.team.datamodel.ApplyRegularSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRegularSubjectRepository extends JpaRepository<ApplyRegularSubjectEntity, Long> {
    List<ApplyRegularSubjectEntity> findByMember(MemberEntity member);

    void deleteByMember(MemberEntity entity);

    List<ApplyRegularSubjectEntity> findBySubject_SubjectId(Long subjectId);
}