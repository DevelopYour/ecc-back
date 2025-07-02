package com.seoultech.ecc.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.ai.config.OpenAiConfig;
import com.seoultech.ecc.ai.dto.AiRequest;
import com.seoultech.ecc.ai.dto.AiResponse;
import com.seoultech.ecc.ai.dto.AiExpressionResponse;
import com.seoultech.ecc.review.dto.ReviewQuestionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAiConfig openAiConfig;
    private final RestTemplate restTemplate;
    private final PromptLoader promptLoader;

    public AiExpressionResponse generateTranslation(String question, boolean korean) {
        try {
            String prompt = promptLoader.createTranslationPrompt(question, korean);
            String response = callOpenAi(prompt);
            return parseExpressionResponse(response);

        } catch (Exception e) {
            log.error("Failed to generate translation for question: {}", question, e);
            throw new RuntimeException("AI translation service failed", e);
        }
    }

    public AiExpressionResponse generateFeedback(String question) {
        try {
            String prompt = promptLoader.createFeedbackPrompt(question);
            String response = callOpenAi(prompt);
            return parseExpressionResponse(response);

        } catch (Exception e) {
            log.error("Failed to generate feedback for question: {}", question, e);
            throw new RuntimeException("AI feedback service failed", e);
        }
    }

    public List<ReviewQuestionDto> gradeTest(List<ReviewQuestionDto> questions) {
        try {
            String prompt = promptLoader.createGradePrompt(questions);
            String response = callOpenAi(prompt);
            return parseTestResponse(response);
        } catch (Exception e) {
            log.error("Failed to grade test for questions: {}", questions, e);
            throw new RuntimeException("AI grade service failed", e);
        }
    }

    private String callOpenAi(String prompt) {
        String url = openAiConfig.getBaseUrl() + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiConfig.getApiKey());

        AiRequest request = AiRequest.builder()
                .model(openAiConfig.getModel())
                .maxTokens(openAiConfig.getMaxTokens())
                .messages(List.of(
                        AiRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .build();

        HttpEntity<AiRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<AiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AiResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            } else {
                throw new RuntimeException("OpenAI API returned unsuccessful response");
            }

        } catch (HttpClientErrorException e) {
            log.error("OpenAI API client error: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("OpenAI API client error: " + e.getMessage(), e);
        } catch (HttpServerErrorException e) {
            log.error("OpenAI API server error: {}", e.getResponseBodyAsString(), e);
            throw new RuntimeException("OpenAI API server error: " + e.getMessage(), e);
        }
    }

    private AiExpressionResponse parseExpressionResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = extractJsonFromResponse(response);
            return objectMapper.readValue(jsonResponse, AiExpressionResponse.class);

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            return null;
        }
    }

    private List<ReviewQuestionDto> parseTestResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = extractJsonFromResponse(response);
            // TypeReference를 사용해서 제네릭 타입 문제 해결
            return objectMapper.readValue(jsonResponse,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ReviewQuestionDto.class));
        } catch (Exception e) {
            log.error("Failed to parse test response: {}", response, e);
            return null;
        }
    }

    private String extractJsonFromResponse(String response) {
        System.out.println(response);
        response = response.trim();

        // 배열인 경우
        int arrayStart = response.indexOf('[');
        int arrayEnd = response.lastIndexOf(']');
        if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
            return response.substring(arrayStart, arrayEnd + 1);
        }

        // 객체인 경우 (기존 로직)
        int objectStart = response.indexOf('{');
        int objectEnd = response.lastIndexOf('}');
        if (objectStart != -1 && objectEnd != -1 && objectEnd > objectStart) {
            return response.substring(objectStart, objectEnd + 1);
        }

        throw new RuntimeException("No valid JSON found in response");
    }
}