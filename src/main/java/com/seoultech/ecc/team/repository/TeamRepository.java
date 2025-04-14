package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
