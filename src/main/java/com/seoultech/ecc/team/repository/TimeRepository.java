package com.seoultech.ecc.team.repository;

import com.seoultech.ecc.team.datamodel.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeRepository extends JpaRepository<TimeEntity, Integer> { // Long -> Integer 변경
}