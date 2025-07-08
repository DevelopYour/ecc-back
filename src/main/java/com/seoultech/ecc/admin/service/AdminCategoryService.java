package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.dto.EditCategoryDto;
import com.seoultech.ecc.admin.dto.TopicCategoryDto;
import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import com.seoultech.ecc.study.repository.TopicCategoryRepository;
import com.seoultech.ecc.study.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final TopicCategoryRepository topicCategoryRepository;
    private final TopicRepository topicRepository;

    public List<TopicCategoryDto> getAllCategories() {
        return topicCategoryRepository.findAll().stream().map(TopicCategoryDto::fromEntity).collect(Collectors.toList());
    }

    public TopicCategoryDto add(EditCategoryDto dto) {
        TopicCategoryEntity entity = TopicCategoryEntity.builder().category(dto.getName()).description(dto.getDescription()).build();
        entity = topicCategoryRepository.save(entity);
        return TopicCategoryDto.fromEntity(entity);
    }

    public TopicCategoryDto update(Integer categoryId, EditCategoryDto dto) {
        TopicCategoryEntity entity = topicCategoryRepository.findById(categoryId).orElseThrow();
        entity.setCategory(dto.getName());
        entity.setDescription(dto.getDescription());
        entity = topicCategoryRepository.save(entity);
        return TopicCategoryDto.fromEntity(entity);
    }

    @Transactional
    public void delete(Integer categoryId) {
        topicRepository.deleteByCategory_Id(categoryId);
        topicCategoryRepository.deleteById(categoryId);
    }
}
