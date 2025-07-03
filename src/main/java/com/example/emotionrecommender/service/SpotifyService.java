package com.example.emotionrecommender.service;

import com.example.emotionrecommender.dto.SongResponse;
import com.example.emotionrecommender.exception.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private final SpotifyAuthService spotifyAuthService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SpotifyService(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    public List<SongResponse> getSongsByMood(String mood) {
        List<SongResponse> songs = new ArrayList<>();
        try {
            String token = spotifyAuthService.getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 🎯 First try searching playlists
            String searchUrl = "https://api.spotify.com/v1/search?q=" + mood + "&type=playlist&limit=3";
            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode playlists = root.at("/playlists/items");
                System.out.println("Playlists node: " + playlists.toPrettyString());

                if (playlists.isArray() && playlists.size() > 0) {
                    for (JsonNode playlist : playlists) {
                        JsonNode idNode = playlist.get("id");
                        if (idNode == null) continue;

                        String playlistId = idNode.asText();
                        String tracksUrl = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks?limit=10";

                        ResponseEntity<String> tracksResponse = restTemplate.exchange(tracksUrl, HttpMethod.GET, entity, String.class);
                        JsonNode tracksRoot = objectMapper.readTree(tracksResponse.getBody());
                        JsonNode items = tracksRoot.get("items");

                        if (items == null || !items.isArray()) continue;

                        for (JsonNode item : items) {
                            JsonNode track = item.get("track");
                            if (track != null && track.get("name") != null && track.get("artists") != null && track.get("external_urls") != null) {
                                String name = track.get("name").asText();
                                String artist = track.get("artists").get(0).get("name").asText();
                                String url = track.get("external_urls").get("spotify").asText();

                                songs.add(new SongResponse(name, artist, url));

                                if (songs.size() >= 10) {
                                    return songs;
                                }
                            }
                        }
                    }
                }
            }

            // 🎯 Fallback: If no playlists or not enough songs, search tracks directly
            if (songs.size() < 5) {
                String trackSearchUrl = "https://api.spotify.com/v1/search?q=" + mood + "&type=track&limit=10";
                ResponseEntity<String> trackResponse = restTemplate.exchange(trackSearchUrl, HttpMethod.GET, entity, String.class);

                if (trackResponse.getStatusCode() == HttpStatus.OK) {
                    JsonNode trackRoot = objectMapper.readTree(trackResponse.getBody());
                    JsonNode trackItems = trackRoot.at("/tracks/items");

                    if (trackItems.isArray() && trackItems.size() > 0) {
                        for (JsonNode track : trackItems) {
                            if (track.get("name") != null && track.get("artists") != null && track.get("external_urls") != null) {
                                String name = track.get("name").asText();
                                String artist = track.get("artists").get(0).get("name").asText();
                                String url = track.get("external_urls").get("spotify").asText();

                                songs.add(new SongResponse(name, artist, url));

                                if (songs.size() >= 10) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (songs.isEmpty()) {
                throw new CustomException("No songs found for mood: " + mood);
            }

            return songs;

        } catch (Exception ex) {
            throw new CustomException("Error fetching songs: " + ex.getMessage());
        }
    }
}
