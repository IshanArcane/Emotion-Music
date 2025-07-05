# 🎧 Emotion-Based Music Recommender 🎶🤖

An AI-powered application that detects your emotion from an image and recommends music tracks that match your mood using Spotify!

---

## 🚀 Features

- 💁 **Emotion Detection**  
  Upload your image and the system detects your dominant emotion using a deep learning model (DeepFace).

- 🎵 **Music Recommendation**  
  Based on the detected emotion, the app fetches curated playlists or tracks from Spotify.

- ⚡ **Fast & Secure**  
  Local Python microservice for emotion detection — no third-party external APIs needed.

---

## 💡 How It Works

1️⃣ Upload a selfie or image.  
2️⃣ Flask-based Python service analyzes your face and returns the detected emotion.  
3️⃣ Spring Boot backend receives this emotion and queries Spotify for relevant songs.  
4️⃣ Enjoy the recommended playlist matching your mood!  

---

## 🛠️ Tech Stack

### Backend

- **Spring Boot (Java)** — core server logic & API
- **Flask (Python)** — microservice for emotion analysis
- **DeepFace (Python)** — emotion detection model
- **Spotify API** — music data & playlists

### Others

- RestTemplate (Spring) for inter-service communication
- JSON (Jackson) parsing
- Secure environment configs using `.env`

---

## ⚙️ Setup & Run

### 1️⃣ Clone the repository

```bash
git clone https://github.com/IshanArcane/Emotion-Music.git
cd Emotion-Music
