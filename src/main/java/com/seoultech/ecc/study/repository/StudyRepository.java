package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {
}
