package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TopicCategoryRepository extends JpaRepository<TopicCategoryEntity,Long> {
}
