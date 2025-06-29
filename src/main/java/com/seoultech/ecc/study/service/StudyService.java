package com.seoultech.ecc.study.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.ai.dto.AiTranslationResponse;
import com.seoultech.ecc.ai.service.OpenAiService;
import com.seoultech.ecc.report.datamodel.ReportDocument;
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
    private OpenAiService openAiService;

    @Autowired
    private StudyRepository studyRepository;

    public List<WeeklySummaryDto> getTeamProgress(Long teamId) {
        List<ReportDocument> reports = reportService.findReportsByTeamId(teamId);
        List<WeeklySummaryDto> dtos = new ArrayList<>();
        for (ReportDocument report : reports) {
            WeeklySummaryDto dto = new WeeklySummaryDto();
            StudySummaryDto studyDto = new StudySummaryDto();
            studyDto.setTeamId(teamId);
            studyDto.setWeek(report.getWeek());
            if (studyRepository.findByStudyId(report.getId()) != null) { // 진행중
                studyDto.setStudyStatus(StudyStatus.ONGOING);
                dto.setReviewSummaries(null);
            } else if(report.isSubmitted()){ // 보고서 제출 완료
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(report.getId()));
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
    public StudyRedis getStudyRoom(Long teamId) {
        // teamId로 이미 진행 중인 study Redis 확인 후 있으면 반환
        String teamStudyKey = "team:" + teamId + ":study";
        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId); // 해당 팀의 스터디 redis 존재 여부 확인
        if (existingStudyId != null) {
            String redisKey = "study:" + existingStudyId;
            StudyRedis existingStudy = studyRepository.findByStudyId(existingStudyId); // 스터디Id로 redis 내용 확인
            if (existingStudy != null) return existingStudy;
            // 예외 처리 (키는 있는데 값은 없는 경우
            throw new IllegalStateException("Study key exists but StudyRedis is null. (studyId=" + existingStudyId + ")");
        }
        // 없으면 생성
        String reportId = reportService.createReport(teamId); // 1. 보고서 초안 생성 TODO: 보고서 초안은 있지만 study redis가 없는 경우 처리 필요 (생성날짜로 판단?)
        StudyRedis studyRedis = new StudyRedis(reportId, teamId, new ArrayList<>()); // 2. Redis Study 객체 생성 (빈 topic 목록)
        studyRepository.save(studyRedis); // 3. Redis 저장
        studyRepository.saveTeamIndex(teamId, reportId);// 인덱싱용 저장
        return studyRedis;
    }

    public StudyRedis addTopicToStudy(String studyId, List<TopicDto> topicDtos) {

        // 1. Redis에서 기존 StudyRedis 객체 불러오기
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);

        // 2. topic 리스트에 추가
        for (TopicDto dto : topicDtos) {
            TopicRedis topic = new TopicRedis();
            Long newTopicId = (long) (study.getTopics().size() + 1);
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

        // 1. Input validation
        validateInput(studyId, questionDto);

        // 2. Find study
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        // 3. Find topic
        TopicRedis targetTopic = findTopicInStudy(study, questionDto.getTopicId());

        // 4. Generate AI translation
        AiTranslationResponse aiResponse = openAiService.generateTranslation(questionDto.getQuestion());

        // 5. Create and add expression
        ExpressionRedis expression = createExpression(targetTopic, questionDto.getQuestion(), aiResponse);
        targetTopic.getExpressions().add(expression);

        // 6. Save updated study
        studyRepository.save(study);

        System.out.printf("Successfully added AI-generated expression for study: %s, topic: %d",
                studyId, questionDto.getTopicId());

        return study;
    }

    private void validateInput(String studyId, ExpressionToAskDto questionDto) {
        if (studyId == null || studyId.trim().isEmpty()) {
            throw new IllegalArgumentException("Study ID cannot be null or empty");
        }
        if (questionDto == null) {
            throw new IllegalArgumentException("Question DTO cannot be null");
        }
        if (questionDto.getQuestion() == null || questionDto.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        if (questionDto.getTopicId() == null) {
            throw new IllegalArgumentException("Topic ID cannot be null");
        }
    }

    private TopicRedis findTopicInStudy(StudyRedis study, Long topicId) {
        return study.getTopics().stream()
                .filter(t -> t.getTopicId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Topic not found in study"));
    }

    private ExpressionRedis createExpression(TopicRedis topic, String question, AiTranslationResponse aiResponse) {
        ExpressionRedis expression = new ExpressionRedis();

        // Generate next sequential ID
        Long newExpressionId = topic.getExpressions().stream()
                .mapToLong(ExpressionRedis::getExpressionId)
                .max()
                .orElse(0L) + 1L;

        expression.setExpressionId(newExpressionId);
        expression.setQuestion(question);
        expression.setKorean(aiResponse.getKorean());
        expression.setEnglish(aiResponse.getEnglish());
        expression.setExample(aiResponse.getExample());

        return expression;
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
        ReportDocument report = reportService.findByReportId(studyId);
        report.setContents(contents);
        reportService.saveReport(report);

        // 4. Redis 키 삭제 (study, team 인덱싱)
        studyRepository.deleteByStudyId(studyId);
        studyRepository.deleteTeamIndex(study.getTeamId());

        return studyId;
    }

    @Transactional
    public String submitReportAndCreateReview(String reportId) {
        ReportDocument report = reportService.findByReportId(reportId);
        report.setSubmitted(true);
        reviewService.createReviews(report);
        return reportService.saveReport(report);
    }

    public ReportDocument getReport(String reportId) {
        return reportService.findByReportId(reportId);
    }
}
