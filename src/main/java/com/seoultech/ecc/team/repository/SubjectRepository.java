package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<SubjectEntity, Integer> { // Long -> Integer 변경
}