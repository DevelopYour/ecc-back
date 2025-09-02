package com.seoultech.ecc.admin.repository;

import com.seoultech.ecc.admin.datamodel.TeamRecruitmentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<TeamRecruitmentStatusEntity, Integer> {

}
