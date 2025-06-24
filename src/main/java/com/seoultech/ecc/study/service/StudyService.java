package com.seoultech.ecc.study.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.report.datamodel.ReportEntity;
import com.seoultech.ecc.report.dto.ReportResponseDto;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.review.service.ReviewService;
import com.seoultech.ecc.study.datamodel.*;
import com.seoultech.ecc.study.repository.StudyRepository;
import com.seoultech.ecc.study.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private StudyRepository studyRepository;

    // Long → Integer 변경
    public List<WeeklySummaryDto> getTeamProgress(Integer teamId) {
        List<ReportResponseDto> reports = reportService.findReportsByTeamId(teamId);
        List<WeeklySummaryDto> dtos = new ArrayList<>();

        for (ReportResponseDto report : reports) {
            WeeklySummaryDto dto = new WeeklySummaryDto();
            StudySummaryDto studyDto = new StudySummaryDto();
            studyDto.setTeamId(teamId);
            studyDto.setWeek(report.getWeek());

            if (studyRepository.findByStudyId(report.getId().toString()) != null) { // 진행중
                studyDto.setStudyStatus(StudyStatus.ONGOING);
                dto.setReviewSummaries(null);
            } else if (report.isSubmitted()) { // 보고서 제출 완료
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                Integer reportId = report.getId();
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(reportId));
            } else { // 보고서 미제출
                studyDto.setStudyStatus(StudyStatus.DRAFTING);
                dto.setReviewSummaries(null);
            }
            dto.setStudySummary(studyDto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public StudyRedis getStudyRoom(Integer teamId) { // Long → Integer 변경
        // teamId로 이미 진행 중인 study Redis 확인 후 있으면 반환
        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId);
        if (existingStudyId != null) {
            StudyRedis existingStudy = studyRepository.findByStudyId(existingStudyId);
            if (existingStudy != null) return existingStudy;

            // 예외 처리 (키는 있는데 값은 없는 경우)
            throw new IllegalStateException("Study key exists but StudyRedis is null. (studyId=" + existingStudyId + ")");
        }

        // 없으면 생성
        Integer reportId = reportService.createReport(teamId); // 1. 보고서 초안 생성 - Integer 반환
        StudyRedis studyRedis = new StudyRedis(reportId.toString(), teamId, new ArrayList<>()); // 2. Redis Study 객체 생성
        studyRepository.save(studyRedis); // 3. Redis 저장
        studyRepository.saveTeamIndex(teamId, reportId.toString()); // 인덱싱용 저장
        return studyRedis;
    }

    // Long → Integer 변경
    public List<TopicRecommendationDto> getTopicRecommendations(Integer teamId) {
        List<TopicRecommendationDto> dtos = new ArrayList<>();
        // TODO: teamId로 스터디 과목 조회
        // TODO: ERD 수정 (Subject_Topic)

        // 자유 주제
        TopicRecommendationDto topic1 = new TopicRecommendationDto();
        topic1.setCategory(TopicCategory.RANDOM);
        List<String> topics1 = new ArrayList<>();
        topics1.add("What's your favorite time of the day and why?");
        topics1.add("Would you rather travel to the past or the future?");
        topics1.add("What's the most useless thing you've ever bought?");
        topic1.setTopic(topics1);

        // 밸런스 게임
        TopicRecommendationDto topic2 = new TopicRecommendationDto();
        topic2.setCategory(TopicCategory.BALANCE_GAME);
        List<String> topics2 = new ArrayList<>();
        topics2.add("No internet for a year vs No friends for a year");
        topics2.add("Have unlimited time vs Have unlimited money");
        topic2.setTopic(topics2);

        dtos.add(topic1);
        dtos.add(topic2);
        return dtos;
    }

    public StudyRedis addTopicToStudy(String studyId, List<TopicDto> topicDtos) {
        // 1. Redis에서 기존 StudyRedis 객체 불러오기
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);

        // 2. topic 리스트에 추가
        for (TopicDto dto : topicDtos) {
            TopicRedis topic = new TopicRedis();
            Integer newTopicId = study.getTopics().size() + 1;
            topic.setTopicId(newTopicId);
            topic.setTopic(dto.getTopic());
            topic.setCategory(dto.getCategory());
            topic.setExpressions(new ArrayList<>());
            study.getTopics().add(topic);
        }

        // 3. 다시 Redis에 저장 (전체 객체 갱신)
        studyRepository.save(study);
        return study;
    }

    @Transactional
    public StudyRedis getAiHelpAndAdd(String studyId, ExpressionToAskDto questionDto) {
        // TODO: AI에게 결과 받아오기

        // 1. study 찾기
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        // 2. topic 찾기
        Integer topicId = questionDto.getTopicId();
        TopicRedis targetTopic = study.getTopics().stream()
                .filter(t -> t.getTopicId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Topic not found in study"));

        // 3. expression
        ExpressionRedis expression = new ExpressionRedis();
        Integer newExpressionId = targetTopic.getExpressions().size() + 1;
        expression.setExpressionId(newExpressionId);
        expression.setQuestion(questionDto.getQuestion());
        expression.setEnglish("monkey"); // TODO: AI에게 결과 받아오기
        expression.setKorean("원숭이"); // TODO: AI에게 결과 받아오기
        expression.setExample("His monkey wanted a banana."); // TODO: AI에게 결과 받아오기

        // 4. 추가
        targetTopic.getExpressions().add(expression);

        // 5. 수정된 Study 전체 다시 저장
        studyRepository.save(study);
        return study;
    }

    @Transactional
    public String finishStudy(String studyId) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        // StudyRedis → JSON 문자열로 변환
        String contents;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            contents = objectMapper.writeValueAsString(study);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert StudyRedis to JSON", e);
        }

        // 3. 보고서 contents에 StudyRedis JSON 문자열 그대로 저장
        // String id를 Integer로 변환
        Integer reportId = Integer.valueOf(studyId);
        ReportEntity report = reportService.findEntityByReportId(reportId);
        report.setContents(contents);
        reportService.saveReport(report);

        // 4. Redis 키 삭제 (study, team 인덱싱)
        studyRepository.deleteByStudyId(studyId);
        studyRepository.deleteTeamIndex(study.getTeamId());

        return studyId;
    }

    @Transactional
    public String submitReportAndCreateReview(Integer reportId) { // String → Integer 변경
        ReportEntity report = reportService.findEntityByReportId(reportId);
        report.setSubmitted(true);
        reviewService.createReviews(report);
        Integer savedId = reportService.saveReport(report);
        return savedId.toString(); // 호환성을 위해 String으로 변환하여 반환
    }

    public ReportResponseDto getReport(Integer reportId) { // String → Integer 변경
        return reportService.findByReportId(reportId);
    }
}