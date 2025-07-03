package com.example.emotionrecommender.dto;

import org.springframework.web.multipart.MultipartFile;

public class EmotionRequest {
    private String text;
    private MultipartFile image;

    // Getters & Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
