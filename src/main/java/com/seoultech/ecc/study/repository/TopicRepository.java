package com.seoultech.ecc.study.repository;

import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import com.seoultech.ecc.study.datamodel.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
    List<TopicEntity> findByCategory_Id(Long categoryId);
}
