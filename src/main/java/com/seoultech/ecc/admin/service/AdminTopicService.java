package com.seoultech.ecc.admin.service;

import com.seoultech.ecc.admin.dto.AddTopicDto;
import com.seoultech.ecc.admin.dto.TopicDetailDto;
import com.seoultech.ecc.study.datamodel.TopicCategoryEntity;
import com.seoultech.ecc.study.datamodel.TopicEntity;
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
public class AdminTopicService {
    private final TopicRepository topicRepository;
    private final TopicCategoryRepository topicCategoryRepository;

    public List<TopicDetailDto> getAllTopics() {
        return topicRepository.findAll().stream().map(TopicDetailDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public TopicDetailDto add(AddTopicDto dto) {
        TopicCategoryEntity category = topicCategoryRepository.findById(dto.getCategoryId()).get();
        TopicEntity entity = TopicEntity.builder().category(category).topic(dto.getTopic()).build();
        entity = topicRepository.save(entity);
        return TopicDetailDto.fromEntity(entity);
    }

    @Transactional
    public TopicDetailDto update(Integer topicId, String topic) {
        TopicEntity entity = topicRepository.findById(topicId).get();
        entity.setTopic(topic);
        entity = topicRepository.save(entity);
        return TopicDetailDto.fromEntity(entity);
    }

    public void delete(Integer categoryId) {
        topicRepository.deleteById(categoryId);
    }
}
