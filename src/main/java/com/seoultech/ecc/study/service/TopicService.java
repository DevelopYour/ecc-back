package com.seoultech.ecc.study.service;

import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import com.seoultech.ecc.study.datamodel.TopicEntity;
import com.seoultech.ecc.study.dto.TopicDto;
import com.seoultech.ecc.study.dto.TopicSetDto;
import com.seoultech.ecc.study.repository.TopicCategoryRepository;
import com.seoultech.ecc.study.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TopicService {
    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicCategoryRepository topicCategoryRepository;

    public List<TopicSetDto> getAllTopics() {
        List<TopicCategoryEntity> categoryEntities = topicCategoryRepository.findAll();
        return topicCategoriesfromEntityToDto(categoryEntities);
    }

    private List<TopicSetDto> topicCategoriesfromEntityToDto(List<TopicCategoryEntity> categoryEntities) {
        List<TopicSetDto> topicSetDtos = new ArrayList<>();
        for (TopicCategoryEntity categoryEntity : categoryEntities) {
            List<TopicDto> topicDtos = new ArrayList<>();
            List<TopicEntity> topicEntities = topicRepository.findByCategory_Id(categoryEntity.getId());
            for (TopicEntity topicEntity : topicEntities) {
                topicDtos.add(TopicDto.builder()
                        .id(topicEntity.getId())
                        .topic(topicEntity.getTopic())
                        .category(categoryEntity.getCategory())
                        .build());
            }
            topicSetDtos.add(TopicSetDto.builder()
                    .id(categoryEntity.getId())
                    .category(categoryEntity.getCategory())
                    .description(categoryEntity.getDescription())
                    .topics(topicDtos)
                    .build());
        }
        return topicSetDtos;
    }

}
