package com.seoultech.ecc.repository;

import com.seoultech.ecc.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
