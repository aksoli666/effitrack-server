package com.effitrack.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiAnalysisService {

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-3.5-flash:generateContent?key=";

    private static final String AI_SYSTEM_PROMPT =
            "Ти — експерт з ремонту промислового обладнання EffiTrack. " +
                    "Дай короткий аналіз поломки у 2-3 реченнях: " +
                    "1) Орієнтовний час ремонту. " +
                    "2) Можлива вартість (низька/середня/висока). " +
                    "3) Головна рекомендація для майстра (що перевірити в першу чергу). " +
                    "Відповідь має бути виключно українською мовою, лаконічною та без зайвого вступу.";
    private static final String PROMPT_INPUT_PREFIX = "\n\nВхідні дані:\n";
    private static final String PROMPT_EQUIPMENT_PREFIX = "\nНазва верстата: ";
    private static final String REQ_CONTENTS = "contents";
    private static final String REQ_PARTS = "parts";
    private static final String REQ_TEXT = "text";
    private static final String RESP_CANDIDATES = "candidates";
    private static final String RESP_CONTENT = "content";
    private static final String RESP_PARTS = "parts";
    private static final String RESP_TEXT = "text";
    private static final String ERROR_MESSAGE = "Аналіз тимчасово недоступний через технічні причини. Перевірте з'єднання або зверніться до інструкції верстата.";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    public AiAnalysisService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateFixAnalysis(String equipmentName, String faultContext) {
        String url = GEMINI_URL + apiKey;
        String fullPrompt = AI_SYSTEM_PROMPT + PROMPT_INPUT_PREFIX + faultContext + PROMPT_EQUIPMENT_PREFIX + equipmentName;

        try {
            JsonNode requestBody = objectMapper.createObjectNode()
                    .set(REQ_CONTENTS, objectMapper.createArrayNode().add(
                            objectMapper.createObjectNode().set(REQ_PARTS, objectMapper.createArrayNode().add(
                                    objectMapper.createObjectNode().put(REQ_TEXT, fullPrompt)
                            ))
                    ));

            String responseJson = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(responseJson);
            return root.path(RESP_CANDIDATES)
                    .get(0)
                    .path(RESP_CONTENT)
                    .path(RESP_PARTS)
                    .get(0)
                    .path(RESP_TEXT)
                    .asText()
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR_MESSAGE;
        }
    }
}
