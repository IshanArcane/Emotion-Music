package com.example.emotionrecommender.controller;

import com.example.emotionrecommender.dto.EmotionResponse;
import com.example.emotionrecommender.dto.SongResponse;
import com.example.emotionrecommender.exception.CustomException;
import com.example.emotionrecommender.service.ClarifaiService;
import com.example.emotionrecommender.service.SpotifyService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/emotion")
public class EmotionController {

    private final ClarifaiService clarifaiService;
    private final SpotifyService spotifyService;

    public EmotionController(ClarifaiService clarifaiService, SpotifyService spotifyService) {
        this.clarifaiService = clarifaiService;
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
                emotion = clarifaiService.detectEmotion(imageBytes);
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
