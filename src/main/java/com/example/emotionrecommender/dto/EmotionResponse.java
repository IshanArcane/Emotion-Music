package com.example.emotionrecommender.dto;

import java.util.List;

public class EmotionResponse {
    private String emotion;
    private List<SongResponse> songs;

    public EmotionResponse() {}

    public EmotionResponse(String emotion, List<SongResponse> songs) {
        this.emotion = emotion;
        this.songs = songs;
    }

    // Getters & Setters
    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public List<SongResponse> getSongs() {
        return songs;
    }

    public void setSongs(List<SongResponse> songs) {
        this.songs = songs;
    }
}
