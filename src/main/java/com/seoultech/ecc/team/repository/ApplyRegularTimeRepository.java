package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.member.datamodel.MemberEntity;
import com.seoultech.ecc.team.datamodel.ApplyRegularTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRegularTimeRepository extends JpaRepository<ApplyRegularTimeEntity, Integer> {

    List<ApplyRegularTimeEntity> findByMember(MemberEntity member);

    void deleteByMember(MemberEntity member);
}