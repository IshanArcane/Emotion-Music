package com.example.emotionrecommender.service;

import com.example.emotionrecommender.exception.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class ClarifaiService {

    @Value("${clarifai.pat}")
    private String pat;

    @Value("${clarifai.user-id}")
    private String userId;

    @Value("${clarifai.app-id}")
    private String appId;

    @Value("${clarifai.model-id}")
    private String modelId;

    @Value("${clarifai.model-version-id}")
    private String modelVersionId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String detectEmotion(byte[] imageBytes) {
        try {
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

            String url = "https://api.clarifai.com/v2/models/" + modelId + "/versions/" + modelVersionId + "/outputs";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(pat);

            String body = """
            {
              "user_app_id": {
                "user_id": "%s",
                "app_id": "%s"
              },
              "inputs": [
                {
                  "data": {
                    "image": {
                      "base64": "%s"
                    }
                  }
                }
              ]
            }
            """.formatted(userId, appId, imageBase64);

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode concepts = root.at("/outputs/0/data/concepts");
                if (concepts.isArray() && concepts.size() > 0) {
                    // Example: taking first concept name as "emotion"
                    String emotion = concepts.get(0).get("name").asText();
                    return emotion;
                }
                throw new CustomException("No emotion detected in image");
            } else {
                throw new CustomException("Clarifai API returned error: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            throw new CustomException("Error processing image: " + ex.getMessage());
        }
    }
}
