package com.example.emotionrecommender.dto;

public class SongResponse {
    private String name;
    private String artist;
    private String url;

    public SongResponse() {}

    public SongResponse(String name, String artist, String url) {
        this.name = name;
        this.artist = artist;
        this.url = url;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
