package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByStudentNo(String studentNo);
}
