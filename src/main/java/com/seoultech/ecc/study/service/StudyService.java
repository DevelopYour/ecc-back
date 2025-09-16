package com.seoultech.ecc.study.service;

import com.seoultech.ecc.ai.dto.AiExpressionResponse;
import com.seoultech.ecc.ai.service.OpenAiService;
import com.seoultech.ecc.report.datamodel.ReportDocument;
import com.seoultech.ecc.report.dto.ReportFeedbackDto;
import com.seoultech.ecc.report.dto.ReportTopicDto;
import com.seoultech.ecc.report.dto.ReportTranslationDto;
import com.seoultech.ecc.report.service.ReportService;
import com.seoultech.ecc.review.service.ReviewService;
import com.seoultech.ecc.study.datamodel.*;
import com.seoultech.ecc.study.repository.StudyRepository;
import com.seoultech.ecc.study.dto.*;
import com.seoultech.ecc.team.service.SubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudyService {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private SubjectService subjectService;

    public List<WeeklySummaryDto> getTeamProgress(Integer teamId) {
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
                studyDto.setReportId(report.getId());
                studyDto.setStudyStatus(StudyStatus.COMPLETE);
                dto.setReviewSummaries(reviewService.getReviewStatusInfos(report.getId()));
            } else { // 보고서 미제출
                studyDto.setReportId(report.getId());
                studyDto.setStudyStatus(StudyStatus.DRAFTING);
                dto.setReviewSummaries(null);
            }
            dto.setStudySummary(studyDto);
            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public StudyEnterDto getStudyRoom(Integer teamId) {
        StudyEnterDto studyEnterDto = new StudyEnterDto();
        studyEnterDto.setIsGeneral(subjectService.isGeneralTeam(teamId));

        String existingStudyId = studyRepository.findStudyIdByTeamId(teamId);
        if (existingStudyId != null) {
            studyEnterDto.setStudyId(existingStudyId);
            return studyEnterDto;
        }

        log.info("team " + teamId + "의 study redis 생성 시작");
        try {
            String reportId = reportService.createReport(teamId);

            if (studyEnterDto.getIsGeneral()) {
                // 일반 과목 - GeneralRedis 초기화 (모든 리스트 초기화 필요)
                GeneralRedis general = new GeneralRedis();
                general.setId("general_1");
                general.setCorrections(new ArrayList<>());
                general.setVocabs(new ArrayList<>());
                general.setExpressions(new ArrayList<>());

                StudyRedis studyRedis = new StudyRedis(reportId, teamId, null, general);
                studyRepository.save(studyRedis);
            } else {
                StudyRedis studyRedis = new StudyRedis(reportId, teamId, new ArrayList<>(), null);
                studyRepository.save(studyRedis);
            }

            studyRepository.saveTeamIndex(teamId, reportId);
            studyEnterDto.setStudyId(reportId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return studyEnterDto;
    }

    @Transactional
    public StudyRedis getStudyData(String studyId) {
        return studyRepository.findByStudyId(studyId);
    }

    @Transactional
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
        validateInput(studyId, questionDto); // 1. Input validation

        StudyRedis study = studyRepository.findByStudyId(studyId); // 2. Find study
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        TopicRedis targetTopic = findTopicInStudy(study, questionDto.getTopicId()); // 3. Find topic

        // 4. Generate AI translation
        AiExpressionResponse aiResponse = questionDto.isTranslation() ? // 질문 유형(번역/피드백)에 따라 요청 처리
                openAiService.generateTranslation(questionDto.getQuestion(), questionDto.isKorean()) :
                openAiService.generateFeedback(questionDto.getQuestion());

        // 5. Create and add expression
        ExpressionRedis expression = createExpression(targetTopic, questionDto, aiResponse);
        targetTopic.getExpressions().add(expression);

        // 6. Save updated study
        studyRepository.save(study);

        System.out.printf("Successfully added AI-generated expression for study: %s, topic: %d",
                studyId, questionDto.getTopicId());

        return study;
    }


    @Transactional
    public StudyRedis addCorrectionsToGeneralStudy(String studyId, List<CorrectionRedis> correctionDtos) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        GeneralRedis general = study.getGeneral();
        if (general == null) {
            throw new IllegalArgumentException("This is not a general subject study");
        }

        // corrections 리스트가 null인 경우 초기화
        if (general.getCorrections() == null) {
            general.setCorrections(new ArrayList<>());
        }

        // 오답 추가
        for (CorrectionRedis dto : correctionDtos) {
            CorrectionRedis correction = new CorrectionRedis();
            correction.setId("correction_" + System.currentTimeMillis() + "_" + Math.random());
            correction.setQuestion(dto.getQuestion());
            correction.setAnswer(dto.getAnswer());
            correction.setDescription(dto.getDescription());
            general.getCorrections().add(correction);
        }

        studyRepository.save(study);
        return study;
    }

    @Transactional
    public StudyRedis addVocabsToGeneralStudy(String studyId, List<VocabRedis> vocabDtos) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        GeneralRedis general = study.getGeneral();
        if (general == null) {
            throw new IllegalArgumentException("This is not a general subject study");
        }

        // vocabs 리스트가 null인 경우 초기화
        if (general.getVocabs() == null) {
            general.setVocabs(new ArrayList<>());
        }

        // 단어 추가
        for (VocabRedis dto : vocabDtos) {
            VocabRedis vocab = new VocabRedis();
            vocab.setId("vocab_" + System.currentTimeMillis() + "_" + Math.random());
            vocab.setEnglish(dto.getEnglish());
            vocab.setKorean(dto.getKorean());
            general.getVocabs().add(vocab);
        }

        studyRepository.save(study);
        return study;
    }

    @Transactional
    public StudyRedis getGeneralAiHelpAndAdd(String studyId, ExpressionToAskDto questionDto) {
        validateGeneralInput(studyId, questionDto);

        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) {
            throw new IllegalArgumentException("Study not found for id: " + studyId);
        }

        GeneralRedis general = study.getGeneral();
        if (general == null) {
            throw new IllegalArgumentException("This is not a general subject study");
        }

        // expressions 리스트가 null인 경우 초기화 (이 부분이 핵심!)
        if (general.getExpressions() == null) {
            general.setExpressions(new ArrayList<>());
        }

        // AI 응답 생성
        AiExpressionResponse aiResponse = questionDto.isTranslation() ?
                openAiService.generateTranslation(questionDto.getQuestion(), questionDto.isKorean()) :
                openAiService.generateFeedback(questionDto.getQuestion());

        // Expression 생성 및 추가
        ExpressionRedis expression = createGeneralExpression(general, questionDto, aiResponse);
        general.getExpressions().add(expression);

        studyRepository.save(study);

        log.info("Successfully added AI-generated expression for general study: {}", studyId);

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

    private void validateGeneralInput(String studyId, ExpressionToAskDto questionDto) {
        if (studyId == null || studyId.trim().isEmpty()) {
            throw new IllegalArgumentException("Study ID cannot be null or empty");
        }
        if (questionDto == null) {
            throw new IllegalArgumentException("Question DTO cannot be null");
        }
        if (questionDto.getQuestion() == null || questionDto.getQuestion().trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }
        // 일반 과목에서는 topicId가 필요없음
    }

    private TopicRedis findTopicInStudy(StudyRedis study, Integer topicId) {
        return study.getTopics().stream()
                .filter(t -> t.getTopicId().equals(topicId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Topic not found in study"));
    }

    private ExpressionRedis createExpression(TopicRedis topic, ExpressionToAskDto questionDto, AiExpressionResponse aiResponse) {
        ExpressionRedis expression = new ExpressionRedis();

        // Generate next sequential ID
        Integer newExpressionId = topic.getExpressions().stream()
                .mapToInt(ExpressionRedis::getExpressionId)
                .max()
                .orElse(0) + 1;

        expression.setExpressionId(newExpressionId);
        expression.setKorean(aiResponse.getKorean());
        expression.setEnglish(aiResponse.getEnglish());
        expression.setTranslation(questionDto.isTranslation());
        if (questionDto.isTranslation()) { // 번역
            expression.setExampleEnglish(aiResponse.getExampleEnglish());
            expression.setExampleKorean(aiResponse.getExampleKorean());
        } else { // 교정
            expression.setFeedback(aiResponse.getFeedback());
            expression.setOriginal(questionDto.getQuestion());
        }
        return expression;
    }

    private ExpressionRedis createGeneralExpression(GeneralRedis general, ExpressionToAskDto questionDto, AiExpressionResponse aiResponse) {
        ExpressionRedis expression = new ExpressionRedis();

        // Generate next sequential ID for general expressions
        Integer newExpressionId = general.getExpressions().stream()
                .mapToInt(ExpressionRedis::getExpressionId)
                .max()
                .orElse(0) + 1;

        expression.setExpressionId(newExpressionId);
        expression.setKorean(aiResponse.getKorean());
        expression.setEnglish(aiResponse.getEnglish());
        expression.setTranslation(questionDto.isTranslation());

        if (questionDto.isTranslation()) { // 번역
            expression.setExampleEnglish(aiResponse.getExampleEnglish());
            expression.setExampleKorean(aiResponse.getExampleKorean());
        } else { // 교정
            expression.setFeedback(aiResponse.getFeedback());
            expression.setOriginal(questionDto.getQuestion());
        }
        return expression;
    }

    @Transactional
    public String finishStudy(String studyId) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);
        ReportDocument report = reportService.findByReportId(studyId);

        // 회화 과목인지 일반 과목인지 확인 후 분기
        if (study.getTopics() != null && !study.getTopics().isEmpty()) {
            // 회화 과목
            report.setTopics(fromRedisToDocument(study));
        } else if (study.getGeneral() != null) {
            // 일반 과목
            return finishGeneralStudy(studyId);
        }

        reportService.saveReport(report);

        // Redis 키 삭제
        studyRepository.deleteByStudyId(studyId);
        studyRepository.deleteTeamIndex(study.getTeamId());
        return studyId;
    }

    @Transactional
    public String finishGeneralStudy(String studyId) {
        StudyRedis study = studyRepository.findByStudyId(studyId);
        if (study == null) throw new IllegalArgumentException("Study not found for id: " + studyId);
        ReportDocument report = reportService.findByReportId(studyId);
        GeneralRedis general = study.getGeneral();
        // 1. corrections
        report.setCorrections(general.getCorrections().stream().map(CorrectionRedis::toCorrectionDto).collect(Collectors.toList()));
        // 2. vocabs
        report.setVocabs(general.getVocabs().stream().map(VocabRedis::toVocabDto).collect(Collectors.toList()));

        // 3. translations
        // 4. feedbacks
        Map<Boolean, List<ExpressionRedis>> grouped = general.getExpressions()
                .stream()
                .collect(Collectors.partitioningBy(ExpressionRedis::isTranslation));
        report.setTranslations(grouped.get(true).stream()
                .map(ReportTranslationDto::fromRedis)
                .collect(Collectors.toList()));
        report.setFeedbacks(grouped.get(false).stream()
                .map(ReportFeedbackDto::fromRedis)
                .collect(Collectors.toList()));
        reportService.saveReport(report);

        // Redis 키 삭제 (study, team 인덱싱)
        studyRepository.deleteByStudyId(studyId);
        studyRepository.deleteTeamIndex(study.getTeamId());
        return studyId; // == reportId
    }

    @Transactional
    public String submitReportAndCreateReview(String reportId) {
        ReportDocument report = reportService.findByReportId(reportId);
        report.setSubmitted(true);
        report.setSubmittedAt(LocalDateTime.now());
        reviewService.createReviews(report);
        return reportService.saveReport(report);
    }

    public ReportDocument getReport(String reportId) {
        return reportService.findByReportId(reportId);
    }

    private List<ReportTopicDto> fromRedisToDocument(StudyRedis study) {
        return study.getTopics().stream()
                .map(topic -> {
                    Map<Boolean, List<ExpressionRedis>> grouped = topic.getExpressions()
                            .stream()
                            .collect(Collectors.partitioningBy(ExpressionRedis::isTranslation));

                    return ReportTopicDto.builder()
                            .category(topic.getCategory())
                            .topic(topic.getTopic())
                            .feedbacks(grouped.get(false).stream()
                                    .map(ReportFeedbackDto::fromRedis)
                                    .collect(Collectors.toList()))
                            .translations(grouped.get(true).stream()
                                    .map(ReportTranslationDto::fromRedis)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }


}