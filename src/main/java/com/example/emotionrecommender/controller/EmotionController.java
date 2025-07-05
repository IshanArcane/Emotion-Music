package com.example.emotionrecommender.controller;

import com.example.emotionrecommender.dto.EmotionResponse;
import com.example.emotionrecommender.dto.SongResponse;
import com.example.emotionrecommender.exception.CustomException;
import com.example.emotionrecommender.service.PythonEmotionService;
import com.example.emotionrecommender.service.SpotifyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/emotion")
public class EmotionController {

    private final PythonEmotionService pythonEmotionService;
    private final SpotifyService spotifyService;

    public EmotionController(PythonEmotionService pythonEmotionService, SpotifyService spotifyService) {
        this.pythonEmotionService = pythonEmotionService;
        this.spotifyService = spotifyService;
    }

    @PostMapping(value = "/detect", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public EmotionResponse detectEmotion(
            @RequestPart(required = false) MultipartFile image,
            @RequestParam(required = false) String text) {

        String emotion;

        try {
            if (image != null && !image.isEmpty()) {
                byte[] imageBytes = image.getBytes();
                // Call Python service instead of Clarifai
                emotion = pythonEmotionService.detectEmotionFromImage(imageBytes);
            } else if (text != null && !text.isBlank()) {
                emotion = text.trim().toLowerCase();
            } else {
                throw new CustomException("Please provide either an image or text.");
            }

            List<SongResponse> songs = spotifyService.getSongsByMood(emotion);

            return new EmotionResponse(emotion, songs);

        } catch (IOException e) {
            throw new CustomException("Error reading image file: " + e.getMessage());
        }
    }
}
