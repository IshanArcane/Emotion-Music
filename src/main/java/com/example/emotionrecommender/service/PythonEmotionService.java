package com.example.emotionrecommender.service;

import com.example.emotionrecommender.exception.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class PythonEmotionService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String detectEmotionFromImage(byte[] imageBytes) {
        try {
            // Create ByteArrayResource with custom filename
            ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "image.jpg";
                }
            };

            // Build multipart form
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", imageResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String pythonApiUrl = "http://127.0.0.1:5002/detect_emotion";

            ResponseEntity<String> response = restTemplate.exchange(
                    pythonApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("Python API raw response: " + response.getBody());

            JsonNode responseJson = objectMapper.readTree(response.getBody());
            JsonNode emotionNode = responseJson.get("emotion");

            if (emotionNode == null || emotionNode.asText().isEmpty()) {
                throw new CustomException("Python service did not return emotion correctly.");
            }

            String emotion = emotionNode.asText();
            System.out.println("Extracted emotion: " + emotion);

            return emotion;

        } catch (Exception ex) {
            throw new CustomException("Error calling Python service: " + ex.getMessage());
        }
    }
}
