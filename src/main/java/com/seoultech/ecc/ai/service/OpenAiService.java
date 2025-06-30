package com.seoultech.ecc.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoultech.ecc.ai.config.OpenAiConfig;
import com.seoultech.ecc.ai.dto.OpenAiRequest;
import com.seoultech.ecc.ai.dto.OpenAiResponse;
import com.seoultech.ecc.ai.dto.AiExpressionResponse;
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

    public AiExpressionResponse generateTranslation(String question, boolean korean) {
        try {
            String prompt = createTranslationPrompt(question, korean);
            String response = callOpenAi(prompt);
            return parseTranslationResponse(response);

        } catch (Exception e) {
            log.error("Failed to generate translation for question: {}", question, e);
            throw new RuntimeException("AI translation service failed", e);
        }
    }

    public AiExpressionResponse generateFeedback(String question) {
        try {
            String prompt = createFeedbackPrompt(question);
            String response = callOpenAi(prompt);
            return parseTranslationResponse(response);

        } catch (Exception e) {
            log.error("Failed to generate translation for question: {}", question, e);
            throw new RuntimeException("AI translation service failed", e);
        }
    }

    private String createTranslationPrompt(String question, boolean korean) {
        if (korean) {
            return String.format("""
            You are a helpful Korean-English translation assistant.
            Please translate the following Korean text to English and provide an example sentence.
            
            Korean text: "%s"
            
            Respond in the following JSON format:
            {
                "korean": "Original Korean text",
                "english": "English translation of the Korean text",
                "example": "A practical example sentence using the English translation"
            }
            
            Keep translations natural and commonly used.
            Make example sentences simple and practical.
            """, question);
        } else {
            return String.format("""
            You are a helpful English-Korean translation assistant.
            Please translate the following English text to Korean and provide an example sentence.
            
            English text: "%s"
            
            Respond in the following JSON format:
            {
                "korean": "Korean translation of the English text",
                "english": "Original English text", 
                "example": "A practical example sentence using the Korean translation"
            }
            
            Keep translations natural and commonly used.
            Make example sentences simple and practical.
            """, question);
        }
    }

    private String createFeedbackPrompt(String question) {
        return String.format("""
        You are an expert English grammar and writing tutor specializing in helping Korean learners.
        
        Analyze the following English text and provide comprehensive feedback:
        
        Input text: "%s"
        
        Provide your response in this exact JSON format:
        {
            "english": "The corrected and improved version of the input text",
            "korean": "Natural Korean translation of the corrected English text",
            "feedback": "Detailed explanation in Korean about what was corrected and why"
        }
        
        Instructions:
        1. ALWAYS correct any grammatical errors, spelling mistakes, or awkward phrasing
        2. Improve the text to sound more natural and fluent while preserving the original meaning
        3. In "english": provide the best possible version of the text
        4. In "korean": provide a natural, fluent Korean translation (not literal translation)
        5. In "feedback": explain in Korean what changes were made and why, including:
           - Grammar corrections and explanations
           - Vocabulary improvements
           - Style enhancements
           - If the original was already perfect, explain why it's good
        
        Focus on being educational and encouraging in your feedback.
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

    private AiExpressionResponse parseTranslationResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = extractJsonFromResponse(response);
            return objectMapper.readValue(jsonResponse, AiExpressionResponse.class);

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            return null;
        }
    }

    private String extractJsonFromResponse(String response) {
        int startIndex = response.indexOf('{');
        int endIndex = response.lastIndexOf('}');

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return response.substring(startIndex, endIndex + 1);
        }

        throw new RuntimeException("No valid JSON found in response");
    }

}

