package com.seoultech.ecc.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.ai.config.OpenAiConfig;
import com.seoultech.ecc.ai.dto.OpenAiRequest;
import com.seoultech.ecc.ai.dto.OpenAiResponse;
import com.seoultech.ecc.ai.dto.AiTranslationResponse;
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

    public AiTranslationResponse generateTranslation(String question) {
        try {
            String prompt = createTranslationPrompt(question);
            String response = callOpenAi(prompt);
            return parseTranslationResponse(response);

        } catch (Exception e) {
            log.error("Failed to generate translation for question: {}", question, e);
            throw new RuntimeException("AI translation service failed", e);
        }
    }

    private String createTranslationPrompt(String question) {
        return String.format("""
            You are a helpful English-Korean translation assistant.
            Please translate the following Korean question/phrase to English and provide an example sentence.
            
            Korean text: "%s"
            
            Please respond in the following JSON format:
            {
                "english": "English translation here"
                "example": "An example sentence using the English translation"
            }
            
            Keep the English translation natural and commonly used.
            Make the example sentence simple and practical.
            """, question);
    }

    private String callOpenAi(String prompt) {
        String url = openAiConfig.getBaseUrl() + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiConfig.getApiKey());

        OpenAiRequest request = OpenAiRequest.builder()
                .model(openAiConfig.getModel())
                .maxTokens(openAiConfig.getMaxTokens())  // 응답 길이 제한 (비용 절약)
//                .temperature(openAiConfig.getTemperature())  // 창의성 조절 (0=일관적, 1=창의적)
                .messages(List.of(
                        OpenAiRequest.Message.builder()
                                .role("user")
                                .content(prompt)
                                .build()
                ))
                .build();

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, OpenAiResponse.class);

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

    private AiTranslationResponse parseTranslationResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = extractJsonFromResponse(response);
            return objectMapper.readValue(jsonResponse, AiTranslationResponse.class);

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            return createFallbackResponse(response);
        }
    }

    private String extractJsonFromResponse(String response) {
        // Find JSON object in the response
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        throw new RuntimeException("No valid JSON found in response");
    }

    private AiTranslationResponse createFallbackResponse(String response) {
        return AiTranslationResponse.builder()
                .english("translation")
                .korean("번역")
                .example("This is an example sentence.")
                .build();
    }
}

